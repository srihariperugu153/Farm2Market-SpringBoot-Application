package com.farmtomarket.application.config;

import com.farmtomarket.application.utils.Constant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.SecureRandom;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

    public static final String FARM_2_MARKET = "farm_2_market";

    @Bean
    public OpenAPI customOpenAPI(){
        final String securitySchemeName = Constant.AUTHORIZATION;
        License license = new License();
        license.setName(FARM_2_MARKET);

        final String apiTitle = String.format("%s API", StringUtils.capitalize(FARM_2_MARKET));
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .description("Access token")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                )
                )
                .info(new Info().title(apiTitle).version("1.0").description(FARM_2_MARKET).termsOfService("farming service").license(license));
    }
}
