package com.farmtomarket.application.service;

import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.dto.request.LoginRequestDTO;
import com.farmtomarket.application.dto.request.RegistrationDTO;

public interface UserService {
    String registerUser(RegistrationDTO registrationDTO);

    UserDTO login(LoginRequestDTO loginRequest);
}
