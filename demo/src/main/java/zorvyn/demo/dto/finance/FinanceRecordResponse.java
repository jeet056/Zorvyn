package zorvyn.demo.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import zorvyn.demo.entity.RecordType;

@Builder
public record FinanceRecordResponse(
    Long id,
    BigDecimal amount,
    RecordType type,
    String category,
    LocalDate date,
    String notes,
    Long userId,
    String userName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
