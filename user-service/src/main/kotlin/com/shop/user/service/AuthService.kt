package com.shop.user.service

import com.shop.user.model.*
import com.shop.user.repository.UserRepository
import com.shop.user.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authManager: AuthenticationManager
) {
    fun register(req: RegisterRequest): AuthResponse {
        require(!userRepository.existsByEmail(req.email)) { "Email already in use" }
        val user = User(email = req.email, password = passwordEncoder.encode(req.password), name = req.name)
        userRepository.save(user)
        return AuthResponse(jwtService.generateToken(user.email), user.email, user.name)
    }

    fun login(req: LoginRequest): AuthResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(req.email, req.password))
        val user = userRepository.findByEmail(req.email).orElseThrow()
        return AuthResponse(jwtService.generateToken(user.email), user.email, user.name)
    }
}
