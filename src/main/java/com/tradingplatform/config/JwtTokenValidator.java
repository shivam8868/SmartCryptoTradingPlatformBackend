package com.tradingplatform.config;
import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
// import org.hibernate.mapping.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This class is responsible for validating the JWT token that is sent by the client in the request header. It will check if the token is valid and if it is not expired. If the token is valid, it will allow the request to proceed to the next filter in the chain. If the token is invalid or expired, it will return an unauthorized response to the client.
public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                // get the token from the request header and validate it and if it is valid then allow the request to proceed to the next filter in the chain otherwise return an unauthorized response to the client
        String jwt= request.getHeader(JwtConstant.JWT_HEADER);
        if (jwt !=null) {
            // Bearer token is in the format "Bearer <token>" so we need to remove the "Bearer " prefix from the token before validating it
            jwt=jwt.substring(7); // remove the "Bearer " prefix from the token 

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes()); // create a secret key using the secret key constant that is defined in the JwtConstant class
                // claims is the payload of the token that contains the information about the user and the expiration time of the token and any other information that is required for the authentication process
               
               // Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody(); // parse the token and get the claims from the token
              
               //chatgpt code 

                Claims claims = Jwts.parser()
    .verifyWith(key)   // instead of setSigningKey()
    .build()
    .parseSignedClaims(jwt)
    .getPayload();


                String email = String.valueOf(claims.get("email")); // get the email from the claims and set it in the request attribute so that it can be used in the controller to get the user details from the database and perform the authentication process

                String authorities= String.valueOf(claims.get("authorities")); 
                List<GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities); 

                Authentication auth = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    authoritiesList
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth); // set the authentication in the security context holder so that it can be used in the controller to get the user details from the database and perform the authentication process
            }catch(Exception e) {
                e.printStackTrace(); // this will print the actual error
                throw new RuntimeException("Invalid token");
            }
                
            
        }
        filterChain.doFilter(request, response); // allow the request to proceed to the next filter in the chain
    }

}
