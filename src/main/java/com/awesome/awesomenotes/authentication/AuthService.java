package com.awesome.awesomenotes.authentication;

import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserRepository;
import com.awesome.awesomenotes.user.role.ERole;
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

    private final String INVALID_CREDENTIALS = "Invalid creadentials";

    @Value("${securitate.jwtSecret}")
    private String jwtSecret;
    @Value("${securitate.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Value("${securitate.passwordSalt}")
    private String hashSalt;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    public void registerUser(User user) throws AuthException {
        User savedUser = user.clone();
        savedUser.setPassword(hashString(savedUser.getPassword()));
        if (savedUser.getRoles().size() == 0) {
            savedUser.getRoles().add(ERole.ROLE_USER);
        }
        if (userRepository.existsByEmail(savedUser.getEmail()))
            throw new AuthException("User with email: " + savedUser.getEmail() + " already exists.");
        if (userRepository.existsByUsername(savedUser.getUsername()))
            throw new AuthException("User with username: " + savedUser.getUsername() + " already exists.");
        userRepository.save(savedUser);
    }

    public User findUserByCreds(User user) throws AuthException {
        User foundedUser = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(
                        () -> new AuthException(
                                INVALID_CREDENTIALS));
        String hashedPassword = hashString(user.getPassword());
        if (!hashedPassword.equals(foundedUser.getPassword()))
            throw new AuthException(
                    INVALID_CREDENTIALS);
        return foundedUser;
    }

    String hashString(String string) {
        try {
            byte[] salt = Base64.getDecoder().decode(hashSalt);
            KeySpec spec = new PBEKeySpec(string.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String hashString = Base64.getEncoder().encodeToString(hash);
            return hashString;
        } catch (Exception e) {
            log.error("Hashing passwrod error", e);
            return "????";
        }
    }

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
