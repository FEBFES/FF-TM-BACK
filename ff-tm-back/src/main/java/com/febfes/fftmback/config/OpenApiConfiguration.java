package com.febfes.fftmback.config;

import com.febfes.fftmback.domain.RoleName;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

@Configuration
@Profile("!test")
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@OpenAPIDefinition
public class OpenApiConfiguration {

    private static final String PROJECTS = "/projects";

    @Value("${custom-headers.user-role}")
    private String userRoleHeader;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("FF-TM-BACK Monolith")
                .description("FF-TM-BACK Monolith Swagger UI")
                .version("1.0.0"));
    }

    @Bean
    public OpenApiCustomizer customHeaderForProjectsEndpoints() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) return;

            for (String path : paths.keySet()) {
                if (path.contains(PROJECTS)) {
                    PathItem pathItem = paths.get(path);
                    for (Operation operation : pathItem.readOperations()) {
                        Parameter headerParam = new Parameter()
                                .in("header")
                                .name(userRoleHeader)
                                .required(true)
                                .description("User role for project access")
                                .schema(new StringSchema()
                                        ._enum(List.of(Arrays.stream(RoleName.values()).map(RoleName::name).toArray(String[]::new))));

                        operation.addParametersItem(headerParam);
                    }
                }
            }
        };
    }
}
