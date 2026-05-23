package com.shop.user.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(@Value("\${jwt.secret}") private val secret: String) {

    private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }
    private val expiration = 86_400_000L // 24h

    fun generateToken(email: String, role: String): String = Jwts.builder()
        .subject(email)
        .claim("role", role)
        .issuedAt(Date())
        .expiration(Date(System.currentTimeMillis() + expiration))
        .signWith(key)
        .compact()

    fun extractEmail(token: String): String = Jwts.parser()
        .verifyWith(key).build()
        .parseSignedClaims(token).payload.subject

    fun isValid(token: String): Boolean = runCatching {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        true
    }.getOrDefault(false)
}
