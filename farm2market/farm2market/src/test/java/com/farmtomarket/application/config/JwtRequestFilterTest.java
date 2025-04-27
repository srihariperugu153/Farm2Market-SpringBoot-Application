package com.farmtomarket.application.config;

import com.farmtomarket.application.dto.DecodeTokenDTO;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.exception.SystemAuthException;
import com.farmtomarket.application.model.User;
import com.farmtomarket.application.repository.UserRepository;
import com.farmtomarket.application.utils.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTest {
    @Mock
    private JWTUtils jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private User user;
    private UserDTO userDTO;
    private DecodeTokenDTO decodeTokenDTO;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        userDTO = new UserDTO();
        userDTO.setUsername("testUser");

        decodeTokenDTO = new DecodeTokenDTO();
        decodeTokenDTO.setSub("testUser");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_invalidToken() throws IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtTokenUtil.getUsernameFromToken(anyString())).thenThrow(new SystemAuthException("Invalid token"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertFalse(stringWriter.toString().contains("Invalid token"));
    }

    @Test
    void doFilterInternal_expiredToken() throws IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtTokenUtil.getUsernameFromToken(anyString())).thenThrow(new ExpiredJwtException(null,null,"Token expired"));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertFalse(stringWriter.toString().contains("Token expired"));
    }

    @Test
    void extractJwtToken(){
        String token = jwtRequestFilter.extractJwtToken("Bearer validToken");
        assertEquals("validToken", token);
    }

    @Test
    void extractJwtToken_invalidHeader(){
        String token = jwtRequestFilter.extractJwtToken("InvalidHeader");
        assertNull(token);
    }

    @Test
    void processJwtToken_invalidToken(){
        assertThrows(ServletException.class, () -> jwtRequestFilter.processJwtToken("invalidToken", request));
    }

    @Test
    void getUser_validUser(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        User result = jwtRequestFilter.getUser("testUser");
        assertEquals(user,result);
    }

    @Test
    void getUser_invalidUser(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(ApplicationException.class, () -> jwtRequestFilter.getUser("invalidUser"));
    }

    @Test
    void authenticateWithJwtToken(){
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("testUser","password",new ArrayList<>());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(jwtTokenUtil.getAuthentication(userDetails)).thenReturn(authenticationToken);

        jwtRequestFilter.authenticateWithJwtToken(userDetails,request);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(authenticationToken,SecurityContextHolder.getContext().getAuthentication());
    }

}
