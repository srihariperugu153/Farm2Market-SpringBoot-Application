package com.farmtomarket.application.config;

import com.farmtomarket.application.dto.DecodeTokenDTO;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.User;
import com.farmtomarket.application.repository.UserRepository;
import com.farmtomarket.application.utils.Constant;
import com.farmtomarket.application.utils.JWTUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.SystemException;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JWTUtils jwtTokenUtil;

    private final UserRepository userRepository;

    public JwtRequestFilter(JWTUtils jwtTokenUtil, UserRepository userRepository){
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws  IOException {

        try {
            if(shouldSkipFilter(request)){
                filterChain.doFilter(request,response);
                return;
            }
            String jwtToken = extractJwtToken(request.getHeader(Constant.AUTHORIZATION));
            if(jwtToken != null){
                processJwtToken(jwtToken,request);
            } else {
                throw new SystemAuthException("JWT Token does not begin with Bearer String");
            }
            filterChain.doFilter(request,response);
        } catch (SystemAuthException servletException) {
            handleException(response,new SystemAuthException(servletException.getMessage()));
        } catch (Exception exception){
            handleException(response,new ApplicationException("Invalid Authorization"));
        }

    }

    private void handleException(HttpServletResponse response, RuntimeException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"errorCode\":\"401\",\"message\":\"" + exception.getMessage() + "\"}");
    }

    private boolean shouldSkipFilter(HttpServletRequest request){
        return StringUtils.isBlank(request.getHeader(Constant.AUTHORIZATION));
    }

    String extractJwtToken(String authorizationHeader){
        if(authorizationHeader != null && authorizationHeader.startsWith(Constant.BEARER)){
            return authorizationHeader.replace(Constant.BEARER,"").trim();
        }
        return null;
    }

    void processJwtToken(String jwtToken , HttpServletRequest request) throws ServletException{
        try {
            String userName = getUserNameFromJwtToken(jwtToken);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication()==null){
                User user = getUser(userName);
                if(user != null){
                    validatedTokenAndUserDetails(jwtToken,request,userName,user);
                }
            }
        } catch (ExpiredJwtException e){
            logger.warn(Constant.ACCESS_TOKEN_HAS_EXPIRED + e.getMessage());
            throw new ServletException(Constant.ACCESS_TOKEN_HAS_EXPIRED);
        } catch (Exception e){
            throw new ServletException(e.getMessage());
        }
    }

    private void validatedTokenAndUserDetails(String jwtToken, HttpServletRequest request, String userName, User user) throws ServletException{
        try {
            UserDetails userDetails = createUserDetails(userName, user.getPassword());
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken,userDetails))){
                authenticateWithJwtToken(userDetails,request);
            }
        } catch (Exception e){
            throw new ServletException(Constant.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public User getUser(String userName){
        Optional<User> users = userRepository.findByUsername(userName);
        if (users.isEmpty()){
            throw new ApplicationException(Constant.INVALID_CREDENTIALS);
        }
        return users.get();
    }

    private String getUserNameFromJwtToken(String jwtToken) throws JsonProcessingException{
        DecodeTokenDTO dto = extractTokenDto(jwtToken);
        if (dto.getSub() != null){
            Optional<User> user = userRepository.findByUsername(dto.getSub());
            user.ifPresent(u ->{
                UserDTO userDTO = new UserDTO();
                userDTO.setId(u.getId());
                userDTO.setUsername(u.getUsername());
                userDTO.setFirstName(u.getFirstName());
                userDTO.setLastName((u.getLastName()));
                userDTO.setEmail(u.getEmail());
                userDTO.setCity(u.getCity());
                userDTO.setRoleName(String.valueOf(user.get().getRoleName()));
                UserContextHolder.setUserDto(userDTO);
            });

            return dto.getSub();
        }
        return null;
    }

    private DecodeTokenDTO extractTokenDto(String jwtToken) throws JsonProcessingException{
        String[] split = jwtToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(split[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,true);
        return objectMapper.readValue(payload,DecodeTokenDTO.class);
    }

    private UserDetails createUserDetails(String username, String password){
        return new org.springframework.security.core.userdetails.User(username,password,new ArrayList<>());
    }

    void authenticateWithJwtToken(UserDetails userDetails, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = jwtTokenUtil.getAuthentication(userDetails);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
