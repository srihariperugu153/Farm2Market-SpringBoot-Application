package com.farmtomarket.application.service.serviceImpl;

import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.dto.request.LoginRequestDTO;
import com.farmtomarket.application.dto.request.RegistrationDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.Role;
import com.farmtomarket.application.model.User;
import com.farmtomarket.application.repository.UserRepository;
import com.farmtomarket.application.service.UserService;
import com.farmtomarket.application.utils.Constant;
import com.farmtomarket.application.utils.JWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final ModelMapper modelMapper;

    private final JWTUtils jwtUtils;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder bCryptPasswordEncoder, ModelMapper modelMapper, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public String registerUser(RegistrationDTO registrationDTO) {
        Optional<User> user = userRepository.findByUsername(registrationDTO.getUsername());
        if (user.isEmpty()){
            User newUser = new User();
            newUser.setUsername(registrationDTO.getUsername());
            newUser.setPassword(bCryptPasswordEncoder.encode(registrationDTO.getPassword()));
            newUser.setEmail(registrationDTO.getEmail());
            newUser.setFirstName(registrationDTO.getFirstName());
            newUser.setLastName(registrationDTO.getLastName());
            newUser.setMobileNumber(registrationDTO.getMobileNumber());
            newUser.setCity(registrationDTO.getCity());
            newUser.setState(registrationDTO.getState());
            newUser.setPinCode(registrationDTO.getPinCode());
            newUser.setActive(true);
            newUser.setRoleName(Role.valueOf(registrationDTO.getRoleName()));
            userRepository.save(newUser);
            return Constant.REGISTRATION_SUCCESS_FULL;
        } else {
            return "UserName Already Exists, Please try with different UserName..";
        }

    }

    @Override
    public UserDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()){
            User user = userRepository.findByUsername(loginRequest.getUserName())
                    .orElseThrow(()-> new ApplicationException(Constant.USER_NAME_NOT_FOUND));

            UserDTO userDTO = modelMapper.map(user,UserDTO.class);
            userDTO.setToken(jwtUtils.generateToken(user));
            return userDTO;
        }else {
            throw new SystemAuthException("Invalid Username or Password..");
        }

    }
}
