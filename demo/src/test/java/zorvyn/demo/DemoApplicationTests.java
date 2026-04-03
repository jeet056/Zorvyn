package zorvyn.demo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import zorvyn.demo.dto.finance.FinanceRecordRequest;
import zorvyn.demo.entity.Finance;
import zorvyn.demo.entity.RecordType;
import zorvyn.demo.entity.User;
import zorvyn.demo.repository.FinanceRepository;
import zorvyn.demo.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FinanceRepository financeRepository;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		financeRepository.deleteAll();
	}

	@Test
	void adminCanCreateUser() throws Exception {
		String payload = """
			{
			  "name": "Ops Admin",
			  "email": "ops.admin@zorvyn.io",
			  "password": "Secure@123",
			  "role": "ADMIN",
			  "status": "ACTIVE"
			}
			""";

		mockMvc.perform(post("/api/users")
				.with(httpBasic("admin@zorvyn.io", "Admin@123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.email").value("ops.admin@zorvyn.io"))
			.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	void viewerCannotCreateRecord() throws Exception {
		User analyst = userRepository.findByEmail("analyst@zorvyn.io").orElseThrow();
		FinanceRecordRequest request = new FinanceRecordRequest(
			new BigDecimal("200.00"),
			RecordType.EXPENSE,
			"Operations",
			LocalDate.of(2026, 4, 1),
			"Office rent",
			analyst.getId()
		);

		mockMvc.perform(post("/api/records")
				.with(httpBasic("viewer@zorvyn.io", "Viewer@123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isForbidden());
	}

	@Test
	void analystCanReadRecords() throws Exception {
		User analyst = userRepository.findByEmail("analyst@zorvyn.io").orElseThrow();
		financeRepository.save(Finance.builder()
			.amount(new BigDecimal("1250.00"))
			.type(RecordType.INCOME)
			.category("Consulting")
			.date(LocalDate.of(2026, 3, 15))
			.notes("Client payment")
			.user(analyst)
			.build());

		mockMvc.perform(get("/api/records")
				.with(httpBasic("analyst@zorvyn.io", "Analyst@123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].category").value("Consulting"));
	}

	@Test
	void summaryEndpointReturnsAggregatedTotals() throws Exception {
		User analyst = userRepository.findByEmail("analyst@zorvyn.io").orElseThrow();
		financeRepository.save(Finance.builder()
			.amount(new BigDecimal("3000.00"))
			.type(RecordType.INCOME)
			.category("Sales")
			.date(LocalDate.of(2026, 3, 10))
			.notes("March income")
			.user(analyst)
			.build());
		financeRepository.save(Finance.builder()
			.amount(new BigDecimal("800.00"))
			.type(RecordType.EXPENSE)
			.category("Operations")
			.date(LocalDate.of(2026, 3, 12))
			.notes("Cloud spend")
			.user(analyst)
			.build());

		mockMvc.perform(get("/api/dashboard/summary")
				.with(httpBasic("viewer@zorvyn.io", "Viewer@123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalIncome").value(3000.00))
			.andExpect(jsonPath("$.totalExpense").value(800.00))
			.andExpect(jsonPath("$.netBalance").value(2200.00))
			.andExpect(jsonPath("$.categoryTotals[0].category").exists())
			.andExpect(jsonPath("$.monthlyTrends[0].period").value("2026-03"));
	}

	@Test
	void invalidUserPayloadReturnsValidationError() throws Exception {
		String payload = """
			{
			  "name": "",
			  "email": "invalid-email",
			  "password": "123",
			  "role": null
			}
			""";

		mockMvc.perform(post("/api/users")
				.with(httpBasic("admin@zorvyn.io", "Admin@123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.validationErrors.name").exists())
			.andExpect(jsonPath("$.validationErrors.email").exists())
			.andExpect(jsonPath("$.validationErrors.password").exists());
	}

}
