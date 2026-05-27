package com.shop.user.service

import com.shop.user.model.*
import com.shop.user.repository.RefreshTokenRepository
import com.shop.user.repository.UserRepository
import com.shop.user.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authManager: AuthenticationManager
) {

    @Transactional
    fun register(req: RegisterRequest): AuthResponse {
        require(!userRepository.existsByEmail(req.email)) { "Email already in use" }
        val user = User(email = req.email, password = passwordEncoder.encode(req.password), name = req.name)
        userRepository.save(user)
        val refresh = createRefreshToken(user.email)
        return AuthResponse(
            token = jwtService.generateToken(user.email, user.role.name),
            refreshToken = refresh.token,
            email = user.email,
            name = user.name,
            role = user.role.name
        )
    }

    @Transactional
    fun login(req: LoginRequest): AuthResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(req.email, req.password))
        val user = userRepository.findByEmail(req.email).orElseThrow()
        val refresh = createRefreshToken(user.email)
        return AuthResponse(
            token = jwtService.generateToken(user.email, user.role.name),
            refreshToken = refresh.token,
            email = user.email,
            name = user.name,
            role = user.role.name
        )
    }

    @Transactional
    fun refresh(req: RefreshTokenRequest): AuthResponse {
        val refreshToken = refreshTokenRepository.findByToken(req.refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")
        if (refreshToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken)
            throw IllegalArgumentException("Refresh token expired, please login again")
        }
        val user = userRepository.findByEmail(refreshToken.userEmail)
            .orElseThrow { NoSuchElementException("User not found") }
        // Rotate refresh token
        refreshTokenRepository.delete(refreshToken)
        val newRefresh = createRefreshToken(user.email)
        return AuthResponse(
            token = jwtService.generateToken(user.email, user.role.name),
            refreshToken = newRefresh.token,
            email = user.email,
            name = user.name,
            role = user.role.name
        )
    }

    @Transactional(readOnly = true)
    fun getProfile(email: String): UserProfileResponse =
        userRepository.findByEmail(email)
            .map { it.toProfileResponse() }
            .orElseThrow { NoSuchElementException("User not found") }

    @Transactional
    fun updateProfile(email: String, req: UpdateProfileRequest): UserProfileResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { NoSuchElementException("User not found") }
        req.name?.takeIf { it.isNotBlank() }?.let { user.name = it }
        req.surname?.let { user.surname = it }
        req.patronymic?.let { user.patronymic = it }
        req.phone?.let { user.phone = it }
        req.city?.let { user.city = it }
        req.region?.let { user.region = it }
        req.street?.let { user.street = it }
        req.zipCode?.let { user.zipCode = it }
        return userRepository.save(user).toProfileResponse()
    }

    @Transactional
    fun changePassword(email: String, req: UpdatePasswordRequest) {
        val user = userRepository.findByEmail(email)
            .orElseThrow { NoSuchElementException("User not found") }
        require(passwordEncoder.matches(req.currentPassword, user.password)) { "Wrong current password" }
        user.password = passwordEncoder.encode(req.newPassword)
        userRepository.save(user)
        // Инвалидируем все refresh токены при смене пароля
        refreshTokenRepository.deleteByUserEmail(email)
    }

    @Transactional
    fun updatePhoto(email: String, photoUrl: String): UserProfileResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { NoSuchElementException("User not found") }
        user.profilePhoto = photoUrl
        return userRepository.save(user).toProfileResponse()
    }

    private fun createRefreshToken(email: String): RefreshToken {
        refreshTokenRepository.deleteByUserEmail(email)
        return refreshTokenRepository.save(
            RefreshToken(
                token = UUID.randomUUID().toString(),
                userEmail = email,
                expiresAt = LocalDateTime.now().plusDays(30)
            )
        )
    }
}
