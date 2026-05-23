package com.shop.product.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtService: JwtService) {

    @Bean
    fun jwtAuthFilter(): JwtAuthFilter = JwtAuthFilter(jwtService)

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests { auth ->
            auth
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest().hasRole("ADMIN")
        }
        .exceptionHandling {
            it.authenticationEntryPoint { _, response, _ ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            }
        }
        .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter::class.java)
        .build()
}
