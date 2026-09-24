package zw.ac.uz.dpmds.zoonotic.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the generated OpenAPI metadata and JWT bearer authentication.
 *
 * @author DPDMS
 * @version 1.0
 */
@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * Creates the public API description and bearer security scheme.
     *
     * @return the OpenAPI definition
     */
    @Bean
    public OpenAPI zoonoticDiseaseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DPDMS Zoonotic Disease Service API")
                        .description(
                                "REST API for zoonotic disease incident monitoring "
                                        + "within the Rushinga Provincial Disaster "
                                        + "Monitoring & Management System."))
                .components(new Components()
                        .addSecuritySchemes(
                                BEARER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
