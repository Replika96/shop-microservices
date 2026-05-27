package com.shop.user.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val token: String,

    @Column(nullable = false)
    val userEmail: String,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
