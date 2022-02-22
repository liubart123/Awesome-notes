package com.awesome.awesomenotes.util;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoUtil {

    @Value("${securitate.passwordSalt}")
    private String hashSalt;

    public String hashString(String string) {
        try {
            byte[] salt = Base64.getDecoder().decode(hashSalt);
            KeySpec spec = new PBEKeySpec(string.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            String hashString = Base64.getEncoder().encodeToString(hash);
            return hashString;
        } catch (Exception e) {
            log.error("Hashing passwrod error", e);
            return "";
        }
    }
}
