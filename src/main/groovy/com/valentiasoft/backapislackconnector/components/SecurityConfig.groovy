package com.valentiasoft.backapislackconnector.components

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    JwtCustomFilter jwtCustomFilter

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req ->
                req
                .requestMatchers('/api/v1/slack/**').authenticated()
                .requestMatchers('/api/v1/micro/**').authenticated()
                .requestMatchers('/api/v1/auth/users/**').authenticated()
                .requestMatchers('/api/v1/auth/login/**').permitAll()
                .requestMatchers('/v3/api-docs/**').permitAll()
                .requestMatchers('/swagger-ui/**').permitAll()
            )
        .cors(Customizer.withDefaults())
        .addFilterBefore(jwtCustomFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        return http.build()
    }
}
