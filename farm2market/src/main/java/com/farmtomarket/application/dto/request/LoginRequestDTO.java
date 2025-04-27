package com.farmtomarket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @Schema(description = "Username of the user", example = "srihari153")
    @NotBlank(message = "Username required")
    private String userName;

    @Schema(description = "Password of the user", example = "P@ssword")
    @NotBlank(message = "Password required")
    private String password;
}
