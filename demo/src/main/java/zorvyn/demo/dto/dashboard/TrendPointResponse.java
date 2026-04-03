package zorvyn.demo.dto.dashboard;

import java.math.BigDecimal;

public record TrendPointResponse(
    String period,
    BigDecimal income,
    BigDecimal expense,
    BigDecimal net
) {
}
