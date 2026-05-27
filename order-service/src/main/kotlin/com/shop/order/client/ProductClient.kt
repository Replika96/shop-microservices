package com.shop.order.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

data class ProductStockDto(
    val id: Long = 0,
    val name: String = "",
    val stock: Int = 0
)

@Component
class ProductClient(@Value("\${services.product-url}") private val baseUrl: String) {

    private val client by lazy { RestClient.create(baseUrl) }

    fun getById(id: Long): ProductStockDto =
        try {
            client.get()
                .uri("/api/products/$id")
                .retrieve()
                .body(ProductStockDto::class.java)
                ?: throw NoSuchElementException("Product $id not found")
        } catch (e: RestClientResponseException) {
            if (e.statusCode.value() == 404) throw NoSuchElementException("Product $id not found")
            else throw IllegalStateException("Product service unavailable: ${e.message}")
        }
}
