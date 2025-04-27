package com.farmtomarket.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String roleName;
    private String city;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
    private String token;
}
