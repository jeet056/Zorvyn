package zorvyn.demo.dto.user;

import jakarta.validation.constraints.NotNull;
import zorvyn.demo.entity.Role;

public record UpdateUserRoleRequest(
    @NotNull(message = "Role is required")
    Role role
) {
}
