package zorvyn.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zorvyn.demo.dto.dashboard.CategoryTotalResponse;
import zorvyn.demo.dto.dashboard.DashboardSummaryResponse;
import zorvyn.demo.dto.dashboard.TrendPointResponse;
import zorvyn.demo.dto.finance.FinanceRecordResponse;
import zorvyn.demo.entity.RecordType;
import zorvyn.demo.exception.BadRequestException;
import zorvyn.demo.repository.FinanceRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinanceRepository financeRepository;
    private final FinanceService financeService;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate, int recentLimit) {
        if (recentLimit < 1 || recentLimit > 20) {
            throw new BadRequestException("recentLimit must be between 1 and 20");
        }

        BigDecimal totalIncome = financeRepository.sumByTypeAndDateRange(RecordType.INCOME, startDate, endDate);
        BigDecimal totalExpense = financeRepository.sumByTypeAndDateRange(RecordType.EXPENSE, startDate, endDate);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        List<CategoryTotalResponse> categoryTotals = financeRepository.summarizeByCategory(startDate, endDate);
        List<FinanceRecordResponse> recentActivity = financeService.getRecentRecords(startDate, endDate, recentLimit);
        List<TrendPointResponse> monthlyTrends = financeRepository.summarizeMonthlyTrends(startDate, endDate).stream()
            .map(this::mapTrendPoint)
            .toList();

        return new DashboardSummaryResponse(
            totalIncome,
            totalExpense,
            netBalance,
            categoryTotals,
            recentActivity,
            monthlyTrends
        );
    }

    private TrendPointResponse mapTrendPoint(Object[] row) {
        Integer year = ((Number) row[0]).intValue();
        Integer month = ((Number) row[1]).intValue();
        BigDecimal income = toBigDecimal(row[2]);
        BigDecimal expense = toBigDecimal(row[3]);

        return new TrendPointResponse(
            YearMonth.of(year, month).toString(),
            income,
            expense,
            income.subtract(expense)
        );
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return BigDecimal.valueOf(((Number) value).doubleValue());
    }
}
