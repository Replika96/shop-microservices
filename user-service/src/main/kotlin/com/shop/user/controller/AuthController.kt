package com.shop.user.controller

import com.shop.user.model.*
import com.shop.user.service.AuthService
import com.shop.user.service.PhotoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register, login and profile")
class AuthController(
    private val authService: AuthService,
    private val photoService: PhotoService
) {

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    fun register(@Valid @RequestBody req: RegisterRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.register(req))

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT + refresh token")
    fun login(@Valid @RequestBody req: LoginRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.login(req))

    @PostMapping("/refresh")
    @Operation(summary = "Обновить access token по refresh token")
    fun refresh(@Valid @RequestBody req: RefreshTokenRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.ok(authService.refresh(req))

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

    @PatchMapping("/password")
    @Operation(summary = "Сменить пароль")
    fun changePassword(
        request: HttpServletRequest,
        @Valid @RequestBody req: UpdatePasswordRequest
    ): ResponseEntity<Void> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        authService.changePassword(email, req)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/profile/photo")
    @Operation(summary = "Загрузить фото профиля")
    fun uploadPhoto(
        request: HttpServletRequest,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<UserProfileResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        val url = photoService.upload(file)
        return ResponseEntity.ok(authService.updatePhoto(email, url))
    }
}
