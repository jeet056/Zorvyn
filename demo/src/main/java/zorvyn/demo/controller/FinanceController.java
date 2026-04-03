package zorvyn.demo.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zorvyn.demo.dto.common.PageResponse;
import zorvyn.demo.dto.finance.FinanceRecordRequest;
import zorvyn.demo.dto.finance.FinanceRecordResponse;
import zorvyn.demo.entity.RecordType;
import zorvyn.demo.service.FinanceService;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FinanceRecordResponse createRecord(@Valid @RequestBody FinanceRecordRequest request) {
        return financeService.createRecord(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public PageResponse<FinanceRecordResponse> getRecords(
        @RequestParam(required = false) RecordType type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "date") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        return financeService.getRecords(type, category, startDate, endDate, userId, page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public FinanceRecordResponse getRecord(@PathVariable Long id) {
        return financeService.getRecordById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FinanceRecordResponse updateRecord(@PathVariable Long id, @Valid @RequestBody FinanceRecordRequest request) {
        return financeService.updateRecord(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRecord(@PathVariable Long id) {
        financeService.deleteRecord(id);
    }
}
