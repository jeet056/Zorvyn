package zorvyn.demo.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zorvyn.demo.dto.user.CreateUserRequest;
import zorvyn.demo.dto.user.UpdateUserRoleRequest;
import zorvyn.demo.dto.user.UpdateUserStatusRequest;
import zorvyn.demo.dto.user.UserResponse;
import zorvyn.demo.entity.Status;
import zorvyn.demo.entity.User;
import zorvyn.demo.exception.BadRequestException;
import zorvyn.demo.exception.ResourceNotFoundException;
import zorvyn.demo.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("A user with this email already exists");
        }

        User user = User.builder()
            .name(request.name().trim())
            .email(normalizedEmail)
            .password(passwordEncoder.encode(request.password()))
            .role(request.role())
            .status(request.status() == null ? Status.ACTIVE : request.status())
            .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUserEntity(id));
    }

    @Transactional
    public UserResponse updateRole(Long id, UpdateUserRoleRequest request) {
        User user = findUserEntity(id);
        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = findUserEntity(id);
        user.setStatus(request.status());
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public User findUserEntity(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .role(user.getRole())
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
