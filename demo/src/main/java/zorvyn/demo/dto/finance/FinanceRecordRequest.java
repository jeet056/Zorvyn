package zorvyn.demo.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import zorvyn.demo.entity.RecordType;

public record FinanceRecordRequest(
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,

    @NotNull(message = "Type is required")
    RecordType type,

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must be at most 100 characters")
    String category,

    @NotNull(message = "Date is required")
    LocalDate date,

    @Size(max = 500, message = "Notes must be at most 500 characters")
    String notes,

    @NotNull(message = "User id is required")
    Long userId
) {
}
