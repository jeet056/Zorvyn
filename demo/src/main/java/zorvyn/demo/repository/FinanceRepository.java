package zorvyn.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import zorvyn.demo.dto.dashboard.CategoryTotalResponse;
import zorvyn.demo.entity.Finance;
import zorvyn.demo.entity.RecordType;

public interface FinanceRepository extends JpaRepository<Finance, Long>, JpaSpecificationExecutor<Finance> {

    @Query("""
        select coalesce(sum(f.amount), 0)
        from Finance f
        where f.type = :type
          and (:startDate is null or f.date >= :startDate)
          and (:endDate is null or f.date <= :endDate)
        """)
    BigDecimal sumByTypeAndDateRange(
        @Param("type") RecordType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        select new zorvyn.demo.dto.dashboard.CategoryTotalResponse(f.category, sum(f.amount))
        from Finance f
        where (:startDate is null or f.date >= :startDate)
          and (:endDate is null or f.date <= :endDate)
        group by f.category
        order by sum(f.amount) desc
        """)
    List<CategoryTotalResponse> summarizeByCategory(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        select year(f.date),
               month(f.date),
               sum(case when f.type = zorvyn.demo.entity.RecordType.INCOME then f.amount else 0 end),
               sum(case when f.type = zorvyn.demo.entity.RecordType.EXPENSE then f.amount else 0 end)
        from Finance f
        where (:startDate is null or f.date >= :startDate)
          and (:endDate is null or f.date <= :endDate)
        group by year(f.date), month(f.date)
        order by year(f.date), month(f.date)
        """)
    List<Object[]> summarizeMonthlyTrends(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
