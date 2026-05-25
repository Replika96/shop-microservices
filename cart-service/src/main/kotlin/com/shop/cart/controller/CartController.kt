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
@Tag(name = "Cart", description = "Корзина (работает для гостей через X-Guest-Id header)")
class CartController(private val cartService: CartService) {

    // Вспомогательный: извлечь email (JWT) или guestId (header X-Guest-Id)
    private fun HttpServletRequest.email() = getAttribute("userEmail") as? String
    private fun HttpServletRequest.guestId() = getHeader("X-Guest-Id")

    @GetMapping
    @Operation(summary = "Получить корзину")
    fun getCart(request: HttpServletRequest): ResponseEntity<CartResponse> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(cartService.getCart(email))
            guestId != null -> ResponseEntity.ok(cartService.getGuestCart(guestId))
            else -> ResponseEntity.status(401).build()
        }
    }

    @PostMapping
    @Operation(summary = "Добавить товар в корзину")
    fun addToCart(
        request: HttpServletRequest,
        @Valid @RequestBody req: AddToCartRequest
    ): ResponseEntity<CartResponse> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(cartService.addToCart(email, req))
            guestId != null -> ResponseEntity.ok(cartService.addToGuestCart(guestId, req))
            else -> ResponseEntity.status(401).build()
        }
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Изменить количество товара")
    fun updateQuantity(
        request: HttpServletRequest,
        @PathVariable itemId: Long,
        @Valid @RequestBody req: UpdateQuantityRequest
    ): ResponseEntity<CartResponse> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(cartService.updateQuantity(email, itemId, req))
            guestId != null -> ResponseEntity.ok(cartService.updateGuestQuantity(guestId, itemId, req))
            else -> ResponseEntity.status(401).build()
        }
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Удалить товар из корзины")
    fun removeItem(
        request: HttpServletRequest,
        @PathVariable itemId: Long
    ): ResponseEntity<CartResponse> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(cartService.removeItem(email, itemId))
            guestId != null -> ResponseEntity.ok(cartService.removeGuestItem(guestId, itemId))
            else -> ResponseEntity.status(401).build()
        }
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину")
    fun clearCart(request: HttpServletRequest): ResponseEntity<Void> {
        val email = request.email()
        val guestId = request.guestId()
        when {
            email != null -> cartService.clearCart(email)
            guestId != null -> cartService.clearGuestCart(guestId)
            else -> return ResponseEntity.status(401).build()
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/merge")
    @Operation(summary = "Перенести корзину гостя на аккаунт после логина")
    fun merge(
        request: HttpServletRequest,
        @Valid @RequestBody req: MergeCartRequest
    ): ResponseEntity<CartResponse> {
        val email = request.email() ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(cartService.mergeCart(email, req.guestId))
    }
}
