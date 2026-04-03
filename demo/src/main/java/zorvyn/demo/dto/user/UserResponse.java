package zorvyn.demo.dto.user;

import java.time.LocalDateTime;

import lombok.Builder;
import zorvyn.demo.entity.Role;
import zorvyn.demo.entity.Status;

@Builder
public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    Status status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
