package com.shop.wishlist.controller

import com.shop.wishlist.model.*
import com.shop.wishlist.service.WishlistService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Избранные товары")
class WishlistController(private val wishlistService: WishlistService) {

    @GetMapping
    @Operation(summary = "Получить список избранного")
    fun getWishlist(request: HttpServletRequest): ResponseEntity<List<WishlistItemResponse>> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(wishlistService.getWishlist(email))
    }

    @PostMapping("/toggle")
    @Operation(summary = "Добавить или убрать из избранного")
    fun toggle(
        request: HttpServletRequest,
        @RequestBody req: AddToWishlistRequest
    ): ResponseEntity<Map<String, Any>> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(wishlistService.toggle(email, req))
    }

    @GetMapping("/{productId}/check")
    @Operation(summary = "Проверить есть ли товар в избранном")
    fun check(
        request: HttpServletRequest,
        @PathVariable productId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(mapOf("inWishlist" to wishlistService.isInWishlist(email, productId)))
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить из избранного")
    fun remove(
        request: HttpServletRequest,
        @PathVariable productId: Long
    ): ResponseEntity<Void> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        wishlistService.removeFromWishlist(email, productId)
        return ResponseEntity.noContent().build()
    }
}
