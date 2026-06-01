package com.shop.product.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateProductRequest @JsonCreator constructor(
    @JsonProperty("name") @field:NotBlank val name: String,
    @JsonProperty("description") val description: String = "",
    @JsonProperty("price") @field:Positive val price: BigDecimal,
    @JsonProperty("stock") @field:Min(0) val stock: Int = 0,
    @JsonProperty("category") val category: Category = Category.OTHER,
    @JsonProperty("imageUrl") val imageUrl: String = "",          // обратная совместимость
    @JsonProperty("imageUrls") val imageUrls: List<String> = emptyList()
) {
    // Объединяем оба поля: imageUrls приоритетнее, imageUrl — запасной вариант
    fun resolvedImageUrls(): List<String> = when {
        imageUrls.isNotEmpty() -> imageUrls
        imageUrl.isNotBlank()  -> listOf(imageUrl)
        else                   -> emptyList()
    }
}

data class UpdateProductRequest @JsonCreator constructor(
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("price") @field:Positive val price: BigDecimal? = null,
    @JsonProperty("stock") @field:Min(0) val stock: Int? = null,
    @JsonProperty("category") val category: Category? = null,
    @JsonProperty("imageUrl") val imageUrl: String? = null,       // обратная совместимость
    @JsonProperty("imageUrls") val imageUrls: List<String>? = null
) {
    fun resolvedImageUrls(): List<String>? = when {
        imageUrls != null      -> imageUrls
        imageUrl != null       -> listOf(imageUrl)
        else                   -> null   // null = не трогать изображения
    }
}

data class ProductResponse(
    val id: Long, val name: String, val description: String,
    val price: BigDecimal, val stock: Int,
    val category: Category,
    val imageUrl: String,           // первое фото (обратная совместимость с мобилкой)
    val imageUrls: List<String>     // все фото
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val page: Int,
    val size: Int
)

fun Product.toResponse(): ProductResponse {
    val urls = images.map { it.url }
    return ProductResponse(
        id = id, name = name, description = description,
        price = price, stock = stock, category = category,
        imageUrl = urls.firstOrNull() ?: "",
        imageUrls = urls
    )
}
