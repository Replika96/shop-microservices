package com.shop.wishlist.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtService(@Value("\${jwt.secret}") private val secret: String) {
    private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun extractEmail(token: String): String = Jwts.parser()
        .verifyWith(key).build()
        .parseSignedClaims(token).payload.subject

    fun isValid(token: String): Boolean = runCatching {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
        true
    }.getOrDefault(false)
}
