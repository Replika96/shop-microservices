package com.shop.user.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Email val email: String,
    @field:NotBlank @field:Size(min = 6) val password: String,
    @field:NotBlank val name: String
)

data class LoginRequest(
    @field:Email val email: String,
    @field:NotBlank val password: String
)

data class AuthResponse(val token: String, val email: String, val name: String)

data class UpdateProfileRequest @JsonCreator constructor(
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("surname") val surname: String? = null,
    @JsonProperty("patronymic") val patronymic: String? = null,
    @JsonProperty("phone") val phone: String? = null,
    @JsonProperty("city") val city: String? = null,
    @JsonProperty("region") val region: String? = null,
    @JsonProperty("street") val street: String? = null,
    @JsonProperty("zipCode") val zipCode: String? = null
)

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val phone: String,
    val city: String,
    val region: String,
    val street: String,
    val zipCode: String,
    val role: String
)

fun User.toProfileResponse() = UserProfileResponse(
    id, email, name, surname, patronymic, phone, city, region, street, zipCode, role.name
)