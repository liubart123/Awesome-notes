package com.awesome.awesomenotes.authentication;

import java.util.Date;

import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Setter
@Getter
public class AuthService {

    @Value("${securitate.jwtSecret}")
    private String jwtSecret;
    @Value("${securitate.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    ObjectMapper objectMapper;

    public String generateJwt(User user) throws AuthException {
        String userJson;
        User savedUser = user.clone();
        savedUser.setPassword("");
        try {
            userJson = objectMapper.writeValueAsString(savedUser);
        } catch (JsonProcessingException e) {
            throw new AuthException("User serialization error", e);
        }

        String jwt = Jwts.builder()
                .claim("user", userJson)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(
                        SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode(jwtSecret))
                .compact();
        return jwt;
    }

    public User getUserFromToken(String token) throws AuthException {
        try {
            String json = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("user",
                    String.class);
            User user = objectMapper.readValue(json, User.class);
            if (user == null) {
                throw new AuthException("Invalid token");
            } else {
                return user;
            }
        } catch (ExpiredJwtException e) {
            throw new AuthException("Token is expired");
        } catch (Exception e) {
            throw new AuthException("Invalid token");
        }
    }
}
