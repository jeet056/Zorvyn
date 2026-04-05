package zorvyn.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH_SCHEME = "basicAuth";

    @Bean
    public OpenAPI financeDashboardOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Finance Dashboard API")
                .version("v1")
                .description("Backend APIs for users, financial records, and dashboard summaries"))
            .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SCHEME))
            .components(new Components()
                .addSecuritySchemes(BASIC_AUTH_SCHEME, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")));
    }
}
