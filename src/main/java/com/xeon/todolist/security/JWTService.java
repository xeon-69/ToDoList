package com.xeon.todolist.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class JWTService {

    private final String secret;

    public JWTService(@Value("${jwt.secret}") String secret){
        this.secret = secret;
    }

    public String generateToken(String username) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 3600 * 24))
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey() {
//        byte [] keyBytes = Decoders.BASE64.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
