package com.shop.user.controller

import com.shop.user.model.AuthResponse
import com.shop.user.model.LoginRequest
import com.shop.user.model.RegisterRequest
import com.shop.user.model.UpdateProfileRequest
import com.shop.user.model.UserProfileResponse
import com.shop.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login and profile")
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
    fun getProfile(request: HttpServletRequest): ResponseEntity<UserProfileResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(authService.getProfile(email))
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update profile (name, address, phone)")
    fun updateProfile(
        request: HttpServletRequest,
        @RequestBody req: UpdateProfileRequest
    ): ResponseEntity<UserProfileResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(authService.updateProfile(email, req))
    }
}
