package zorvyn.demo.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import zorvyn.demo.entity.Role;
import zorvyn.demo.entity.Status;
import zorvyn.demo.entity.User;
import zorvyn.demo.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner seedDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            userRepository.save(User.builder()
                .name("System Admin")
                .email("admin@zorvyn.io")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build());

            userRepository.save(User.builder()
                .name("Lead Analyst")
                .email("analyst@zorvyn.io")
                .password(passwordEncoder.encode("Analyst@123"))
                .role(Role.ANALYST)
                .status(Status.ACTIVE)
                .build());

            userRepository.save(User.builder()
                .name("Dashboard Viewer")
                .email("viewer@zorvyn.io")
                .password(passwordEncoder.encode("Viewer@123"))
                .role(Role.VIEWER)
                .status(Status.ACTIVE)
                .build());
        };
    }
}
