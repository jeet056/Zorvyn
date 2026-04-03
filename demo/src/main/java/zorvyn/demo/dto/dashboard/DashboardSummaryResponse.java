package zorvyn.demo.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

import zorvyn.demo.dto.finance.FinanceRecordResponse;

public record DashboardSummaryResponse(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance,
    List<CategoryTotalResponse> categoryTotals,
    List<FinanceRecordResponse> recentActivity,
    List<TrendPointResponse> monthlyTrends
) {
}
