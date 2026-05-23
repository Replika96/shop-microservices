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
        return AuthResponse(jwtService.generateToken(user.email, user.role.name), user.email, user.name)
    }

    fun login(req: LoginRequest): AuthResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(req.email, req.password))
        val user = userRepository.findByEmail(req.email).orElseThrow()
        return AuthResponse(jwtService.generateToken(user.email, user.role.name), user.email, user.name)
    }

    fun getProfile(email: String): UserProfileResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { NoSuchElementException("User not found") }
        return user.toProfileResponse()
    }

    fun updateProfile(email: String, req: UpdateProfileRequest): UserProfileResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { NoSuchElementException("User not found") }
        req.name?.let { user.name = it }
        req.surname?.let { user.surname = it }
        req.patronymic?.let { user.patronymic = it }
        req.phone?.let { user.phone = it }
        req.city?.let { user.city = it }
        req.region?.let { user.region = it }
        req.street?.let { user.street = it }
        req.zipCode?.let { user.zipCode = it }
        return userRepository.save(user).toProfileResponse()
    }
}
