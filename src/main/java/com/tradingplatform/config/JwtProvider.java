package com.tradingplatform.config;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles=populateAuthorities(authorities);


        if (roles.isEmpty()) {
        // ✅ fallback: agar authorities empty hai to default role set karo
        roles = "ROLE_CUSTOMER";
    }

        // depricated  method 
        // String jwt= Jwts.builder()
        // .setIssuedAt(new Date())
        // .setExpiration(new Date(new Date().getTime()+86400000))
        // .claim("email",auth.getName()).claim("authorities",roles)
        // .signWith(key)
        // .compact();

        // chatgpt code for version 0.12.x

        String jwt = Jwts.builder()
        .issuedAt(new Date())   // ✅ new method
        .expiration(new Date(System.currentTimeMillis() + 86400000)) // ✅ new method
        .claim("email", auth.getName())
        .claim("authorities", roles)
        .claim("role", "ROLE_CUSTOMER")
        .signWith(key)
        .compact();

    return jwt;

    }

    public static String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)   // instead of setSigningKey()
            .build()
            .parseSignedClaims(token)
            .getPayload();


        String email = String.valueOf(claims.get("email")); 
        return email;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth = new HashSet<>();
        for(GrantedAuthority authority: authorities) {
            auth.add(authority.getAuthority());
        }
        return String.join(",", auth);
    }
}
