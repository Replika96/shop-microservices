package com.shop.order.client

import com.shop.order.model.CartResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Component
class CartClient(@Value("\${services.cart-url}") private val baseUrl: String) {

    private val client by lazy { RestClient.create(baseUrl) }

    fun getCart(token: String): CartResponseDto =
        try {
            client.get()
                .uri("/api/cart")
                .header("Authorization", token)
                .retrieve()
                .body(CartResponseDto::class.java)
                ?: CartResponseDto()
        } catch (e: RestClientResponseException) {
            throw IllegalStateException("Cart service unavailable: ${e.message}")
        }

    fun clearCart(token: String) {
        try {
            client.delete()
                .uri("/api/cart")
                .header("Authorization", token)
                .retrieve()
                .toBodilessEntity()
        } catch (e: RestClientResponseException) {
            throw IllegalStateException("Cart service unavailable: ${e.message}")
        }
    }
}
