package com.farmtomarket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Native;

@Getter
@Setter
public class RegistrationDTO {
    @Schema(description = "Username of the user", example = "srihari153")
    @NotBlank(message = "UserName cannot be Empty")
    @Size(min = 3, max = 30, message = "Username should be within 3 to 30 characters only")
    private String username;

    @Schema(description = "First Name of the user", example = "Srihari")
    @NotBlank(message = "First Name cannot be Empty")
    private String firstName;

    @Schema(description = "Last Name of the user", example = "Perugu")
    @NotBlank(message = "Last Name cannot be Empty")
    private String lastName;

    @Schema(description = "Email address of the user", example = "srihari@gmail.com")
    @Email(message = "Enter a proper Email")
    private String email;

    @Schema(description = "Mobile number of the User", example = "8688521016")
    @NotBlank(message = "Mobile Number cannot be Empty")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(description = "Role of the user", example = "FARMER")
    @NotBlank(message = "Role cannot be Empty")
    private String roleName;

    @Schema(description = "Password of the user", example = "P@ssword")
    @NotBlank(message = "Password cannot be Empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain at least one digit, one letter, and one special character")
    private String password;

    @Schema(description = "State of the user", example = "Tamil Nadu")
    @NotBlank(message = "State cannot be Empty")
    private String state;

    @Schema(description = "City of the user", example = "Chennai")
    @NotBlank(message = "City cannot be Empty")
    private String city;

    @Schema(description = "Pincode of the user's location", example = "600041")
    @Min(value = 100000, message = "Pincode must be atleast 6 digits")
    @Max(value = 999999, message = "Pincode must be atmost 6 digits")
    private long pinCode;
}
