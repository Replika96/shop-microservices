package com.shop.user.controller

import com.shop.user.model.AuthResponse
import com.shop.user.model.LoginRequest
import com.shop.user.model.RegisterRequest
import com.shop.user.model.UpdateProfileRequest
import com.shop.user.model.UserProfileResponse
import com.shop.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    fun register(@Valid @RequestBody req: RegisterRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.register(req))

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.login(req))

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    fun getProfile(authentication: Authentication): ResponseEntity<UserProfileResponse> =
        ResponseEntity.ok(authService.getProfile(authentication.name))

    @PatchMapping("/profile")
    @Operation(summary = "Update current user profile")
    fun updateProfile(
        authentication: Authentication,
        @RequestBody req: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> =
        ResponseEntity.ok(authService.updateProfile(authentication.name, req))
}
