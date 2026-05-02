package com.shop.cart.controller

import com.shop.cart.model.*
import com.shop.cart.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Корзина пользователя")
class CartController(private val cartService: CartService) {

    @GetMapping
    @Operation(summary = "Получить корзину пользователя")
    fun getCart(
        @Parameter @RequestHeader("X-User-Email") email: String
    ): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.getCart(email))

    @PostMapping
    @Operation(summary = "Добавить товар в корзину")
    fun addToCart(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @Valid @RequestBody req: AddToCartRequest
    ): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.addToCart(email, req))

    @PatchMapping("/{itemId}")
    @Operation(summary = "Изменить количество товара")
    fun updateQuantity(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @PathVariable itemId: Long,
        @Valid @RequestBody req: UpdateQuantityRequest
    ): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.updateQuantity(email, itemId, req))

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удалить товар из корзины")
    fun removeItem(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.removeItem(email, itemId))

    @DeleteMapping
    @Operation(summary = "Очистить корзину")
    fun clearCart(
        @Parameter @RequestHeader("X-User-Email") email: String
    ): ResponseEntity<Void> {
        cartService.clearCart(email)
        return ResponseEntity.noContent().build()
    }
}
