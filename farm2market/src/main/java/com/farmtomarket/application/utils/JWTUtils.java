package com.farmtomarket.application.utils;

import com.farmtomarket.application.exception.ApplicationException;
import com.farmtomarket.application.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JWTUtils implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 12L * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    public String secret;

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims :: getSubject);
    }

    public Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token,Claims :: getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(User user){
        Map<String,Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        claims.put("mobile",user.getMobileNumber());
        claims.put("id",user.getId());
        claims.put("role",user.getRoleName());

        try {
            return getAccessToken(claims,user.getUsername());
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public String getAccessToken(Map<String, Object> claims , String userName){
        try{
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userName)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                    .signWith(SignatureAlgorithm.HS256, secret).compact();
        }catch (Exception e){
            throw new ApplicationException(e.getMessage());
        }

    }

    public UsernamePasswordAuthenticationToken getAuthentication(final UserDetails userDetails){
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(userDetails,"",authorities);
    }



}
