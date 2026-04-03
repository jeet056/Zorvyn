package zorvyn.demo.dto.user;

import jakarta.validation.constraints.NotNull;
import zorvyn.demo.entity.Status;

public record UpdateUserStatusRequest(
    @NotNull(message = "Status is required")
    Status status
) {
}
