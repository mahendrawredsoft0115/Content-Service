package com.project.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/content/upload", "/files/**").permitAll() // Allow unauthenticated access
                        .requestMatchers("/api/content/creator/**").permitAll() // allow fetch APIs
                        .requestMatchers("/api/feed/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
