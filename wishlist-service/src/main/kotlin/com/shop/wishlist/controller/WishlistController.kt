package com.shop.wishlist.controller

import com.shop.wishlist.model.*
import com.shop.wishlist.service.WishlistService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Избранные товары пользователя")
class WishlistController(private val wishlistService: WishlistService) {

    @GetMapping
    @Operation(summary = "Получить список избранного")
    fun getWishlist(
        @Parameter @RequestHeader("X-User-Email") email: String
    ): ResponseEntity<List<WishlistItemResponse>> =
        ResponseEntity.ok(wishlistService.getWishlist(email))

    @PostMapping("/toggle")
    @Operation(summary = "Добавить или убрать из избранного")
    fun toggle(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @RequestBody req: AddToWishlistRequest
    ): ResponseEntity<Map<String, Any>> =
        ResponseEntity.ok(wishlistService.toggle(email, req))

    @GetMapping("/{productId}/check")
    @Operation(summary = "Проверить есть ли товар в избранном")
    fun check(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @PathVariable productId: Long
    ): ResponseEntity<Map<String, Boolean>> =
        ResponseEntity.ok(mapOf("inWishlist" to wishlistService.isInWishlist(email, productId)))

    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить товар из избранного")
    fun remove(
        @Parameter @RequestHeader("X-User-Email") email: String,
        @PathVariable productId: Long
    ): ResponseEntity<Void> {
        wishlistService.removeFromWishlist(email, productId)
        return ResponseEntity.noContent().build()
    }
}
