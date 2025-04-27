package com.farmtomarket.application.controller;

import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.dto.request.LoginRequestDTO;
import com.farmtomarket.application.dto.request.RegistrationDTO;
import com.farmtomarket.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> newRegistration(@RequestBody @Valid RegistrationDTO registrationDTO){
        return ResponseEntity.ok(userService.registerUser(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest){
        return ResponseEntity.ok(userService.login(loginRequest));
    }

}
