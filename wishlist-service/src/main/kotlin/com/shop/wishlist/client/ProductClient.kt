package com.shop.wishlist.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val imageUrl: String,
    val stock: Int
)

@Component
class ProductClient(@Value("\${services.product-url}") private val baseUrl: String) {

    private val client by lazy { RestClient.create(baseUrl) }

    fun getById(id: Long): ProductResponse = try {
        client.get()
            .uri("/api/products/$id")
            .retrieve()
            .body(ProductResponse::class.java)
            ?: throw NoSuchElementException("Product $id not found")
    } catch (e: RestClientResponseException) {
        if (e.statusCode.value() == 404)
            throw NoSuchElementException("Product $id not found")
        else
            throw IllegalStateException("Product service unavailable: ${e.message}")
    }
}
