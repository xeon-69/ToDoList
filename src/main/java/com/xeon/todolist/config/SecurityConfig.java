package com.xeon.todolist.config;

import com.xeon.todolist.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTService jwtService;

    private final UserDetailsService userDetailsService;

//    @Bean
//    @Order(1)
//    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .securityMatcher("/api/auth/**", "/error")
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(request -> request.anyRequest().permitAll())
//                .build();
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
//                .authenticationProvider(authenticationProvider())
//                .oauth2ResourceServer(auth2 -> auth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
//                .build();
//    }

    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                //for separate frontend
//                .cors(cors -> {})
                .securityMatcher("/api/auth/**", "/error",
                        "/",
                        "/index.html",
                        "/login.html",
                        "/js/**",
                        "/css/**",
                        "/images/**")

                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                //for separate frontend
//                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()))
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    //set the password encoder
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    //create password encoder
    @Bean //need this to mark as a bean to autowire
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
//        byte [] secretBytes = Decoders.BASE64.decode((CharSequence) jwtService.getKey());
//        SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(keySpec).build();
        return NimbusJwtDecoder.withSecretKey(jwtService.getKey())
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }


}
