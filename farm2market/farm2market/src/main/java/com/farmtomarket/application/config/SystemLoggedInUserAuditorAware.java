package com.farmtomarket.application.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Optional;

@Component
@EnableJpaAuditing
public class SystemLoggedInUserAuditorAware implements AuditorAware<String> {

    public @NotNull Optional<String> getCurrentAuditor(){
        if (UserContextHolder.getUserDto() != null){
            return Optional.of(UserContextHolder.getUserDto().getEmail());
        }
        else {
            return Optional.empty();
        }
    }
}
