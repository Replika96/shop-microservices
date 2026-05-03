package com.shop.cart.controller

import com.shop.cart.model.*
import com.shop.cart.service.CartService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Корзина пользователя")
class CartController(private val cartService: CartService) {

    @GetMapping
    @Operation(summary = "Получить корзину")
    fun getCart(request: HttpServletRequest): ResponseEntity<CartResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(cartService.getCart(email))
    }

    @PostMapping
    @Operation(summary = "Добавить товар в корзину")
    fun addToCart(
        request: HttpServletRequest,
        @Valid @RequestBody req: AddToCartRequest
    ): ResponseEntity<CartResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(cartService.addToCart(email, req))
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Изменить количество товара")
    fun updateQuantity(
        request: HttpServletRequest,
        @PathVariable itemId: Long,
        @Valid @RequestBody req: UpdateQuantityRequest
    ): ResponseEntity<CartResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(cartService.updateQuantity(email, itemId, req))
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удалить товар из корзины")
    fun removeItem(
        request: HttpServletRequest,
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(cartService.removeItem(email, itemId))
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину")
    fun clearCart(request: HttpServletRequest): ResponseEntity<Void> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        cartService.clearCart(email)
        return ResponseEntity.noContent().build()
    }
}
