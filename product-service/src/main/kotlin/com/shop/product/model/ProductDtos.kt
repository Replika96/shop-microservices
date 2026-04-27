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
    @JsonProperty("imageUrl") val imageUrl: String = ""
)

data class UpdateProductRequest @JsonCreator constructor(
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("price") val price: BigDecimal? = null,
    @JsonProperty("stock") val stock: Int? = null,
    @JsonProperty("category") val category: Category? = null,
    @JsonProperty("imageUrl") val imageUrl: String? = null
)

data class ProductResponse(
    val id: Long, val name: String, val description: String,
    val price: BigDecimal, val stock: Int,
    val category: Category, val imageUrl: String
)

fun Product.toResponse() = ProductResponse(id, name, description, price, stock, category, imageUrl)
