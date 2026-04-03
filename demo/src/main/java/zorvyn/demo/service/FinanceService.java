package zorvyn.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import zorvyn.demo.dto.common.PageResponse;
import zorvyn.demo.dto.finance.FinanceRecordRequest;
import zorvyn.demo.dto.finance.FinanceRecordResponse;
import zorvyn.demo.entity.Finance;
import zorvyn.demo.entity.RecordType;
import zorvyn.demo.entity.User;
import zorvyn.demo.exception.BadRequestException;
import zorvyn.demo.exception.ResourceNotFoundException;
import zorvyn.demo.repository.FinanceRepository;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceRepository financeRepository;
    private final UserService userService;

    @Transactional
    public FinanceRecordResponse createRecord(FinanceRecordRequest request) {
        User user = userService.findUserEntity(request.userId());

        Finance finance = Finance.builder()
            .amount(request.amount())
            .type(request.type())
            .category(request.category().trim())
            .date(request.date())
            .notes(request.notes())
            .user(user)
            .build();

        return toResponse(financeRepository.save(finance));
    }

    @Transactional(readOnly = true)
    public PageResponse<FinanceRecordResponse> getRecords(
        RecordType type,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Long userId,
        int page,
        int size,
        String sortBy,
        String direction
    ) {
        validateDateRange(startDate, endDate);
        validatePagination(page, size);

        Sort sort = Sort.by(resolveDirection(direction), resolveSortField(sortBy));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Finance> records = financeRepository.findAll(buildSpecification(type, category, startDate, endDate, userId), pageable);

        return PageResponse.<FinanceRecordResponse>builder()
            .content(records.getContent().stream().map(this::toResponse).toList())
            .page(records.getNumber())
            .size(records.getSize())
            .totalElements(records.getTotalElements())
            .totalPages(records.getTotalPages())
            .build();
    }

    @Transactional(readOnly = true)
    public FinanceRecordResponse getRecordById(Long id) {
        return toResponse(findFinanceEntity(id));
    }

    @Transactional
    public FinanceRecordResponse updateRecord(Long id, FinanceRecordRequest request) {
        Finance finance = findFinanceEntity(id);
        User user = userService.findUserEntity(request.userId());

        finance.setAmount(request.amount());
        finance.setType(request.type());
        finance.setCategory(request.category().trim());
        finance.setDate(request.date());
        finance.setNotes(request.notes());
        finance.setUser(user);

        return toResponse(financeRepository.save(finance));
    }

    @Transactional
    public void deleteRecord(Long id) {
        Finance finance = findFinanceEntity(id);
        financeRepository.delete(finance);
    }

    @Transactional(readOnly = true)
    public Finance findFinanceEntity(Long id) {
        return financeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id " + id));
    }

    public FinanceRecordResponse toResponse(Finance finance) {
        User user = finance.getUser();
        return FinanceRecordResponse.builder()
            .id(finance.getId())
            .amount(finance.getAmount())
            .type(finance.getType())
            .category(finance.getCategory())
            .date(finance.getDate())
            .notes(finance.getNotes())
            .userId(Objects.nonNull(user) ? user.getId() : null)
            .userName(Objects.nonNull(user) ? user.getName() : null)
            .createdAt(finance.getCreatedAt())
            .updatedAt(finance.getUpdatedAt())
            .build();
    }

    @Transactional(readOnly = true)
    public List<FinanceRecordResponse> getRecentRecords(LocalDate startDate, LocalDate endDate, int limit) {
        validateDateRange(startDate, endDate);
        if (limit < 1 || limit > 20) {
            throw new BadRequestException("recentLimit must be between 1 and 20");
        }

        return financeRepository.findAll(
                buildSpecification(null, null, startDate, endDate, null),
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "date", "createdAt")))
            .getContent()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private Specification<Finance> buildSpecification(
        RecordType type,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Long userId
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase()));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be on or before endDate");
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new BadRequestException("page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("size must be between 1 and 100");
        }
    }

    private Sort.Direction resolveDirection(String direction) {
        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("direction must be ASC or DESC");
        }
    }

    private String resolveSortField(String sortBy) {
        List<String> allowedFields = List.of("date", "amount", "category", "createdAt");
        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException("sortBy must be one of: date, amount, category, createdAt");
        }
        return sortBy;
    }
}
