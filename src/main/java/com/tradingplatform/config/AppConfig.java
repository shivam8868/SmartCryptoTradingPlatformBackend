// package com.TradingPlatform.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
// import org.springframework.web.cors.CorsConfigurationSource;

// @Configuration
// public class AppConfig {
//     // security filter chain method to configure the security settings for the application we are making secure some parts which are essential for the application and allowing some parts to be accessed without authentication
//     @Bean
//     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
//         http.sessionManagement(management->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .authorizeHttpRequests(Authorize->Authorize.requestMatchers("/api/**").authenticated()
//                 .anyRequest().permitAll())
//             .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
//             .csrf(csrf->csrf.disable())
//             .cors(cors->cors.configurationSource(corsConfigurationSource()));
//             // we are adding the jwt token validator filter before the basic authentication filter to validate the token before the basic authentication filter is executed and we are disabling the csrf protection because we are using jwt token for authentication and we are allowing cross-origin requests from the frontend application by configuring the cors configuration source method
//            // http.build() is used to build the security filter chain and return it to the spring security framework to use it for securing the application
//             return http.build();
//     }
//     // cors configuration source method to allow cross-origin requests from the frontend application
//     // it throws cors configuration error if any other frontend application tries to access the backend application without proper configuration
//     private CorsConfigurationSource corsConfigurationSource() {
//         return null;
//     }

// }


package com.tradingplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()   //  signup/signin free
                .requestMatchers("/coins/**").permitAll()
                .requestMatchers("/api/users/**").authenticated() //  user endpoints protected
                .requestMatchers("/api/asset/**").authenticated() //  asset endpoints protected
                .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); //  * allow all origins (frontend)
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
