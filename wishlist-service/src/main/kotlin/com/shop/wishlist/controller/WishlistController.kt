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
@Tag(name = "Wishlist", description = "Избранные товары (работает для гостей через X-Guest-Id header)")
class WishlistController(private val wishlistService: WishlistService) {

    private fun HttpServletRequest.email() = getAttribute("userEmail") as? String
    private fun HttpServletRequest.guestId() = getHeader("X-Guest-Id")

    @GetMapping
    @Operation(summary = "Получить список избранного")
    fun getWishlist(request: HttpServletRequest): ResponseEntity<List<WishlistItemResponse>> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(wishlistService.getWishlist(email))
            guestId != null -> ResponseEntity.ok(wishlistService.getGuestWishlist(guestId))
            else -> ResponseEntity.status(401).build()
        }
    }

    @PostMapping("/toggle")
    @Operation(summary = "Добавить или убрать из избранного")
    fun toggle(
        request: HttpServletRequest,
        @RequestBody req: AddToWishlistRequest
    ): ResponseEntity<Map<String, Any>> {
        val email = request.email()
        val guestId = request.guestId()
        return when {
            email != null -> ResponseEntity.ok(wishlistService.toggle(email, req))
            guestId != null -> ResponseEntity.ok(wishlistService.toggleGuest(guestId, req))
            else -> ResponseEntity.status(401).build()
        }
    }

    @GetMapping("/{productId}/check")
    @Operation(summary = "Проверить есть ли товар в избранном")
    fun check(
        request: HttpServletRequest,
        @PathVariable productId: Long
    ): ResponseEntity<Map<String, Boolean>> {
        val email = request.email()
        val guestId = request.guestId()
        val inWishlist = when {
            email != null -> wishlistService.isInWishlist(email, productId)
            guestId != null -> wishlistService.isInGuestWishlist(guestId, productId)
            else -> return ResponseEntity.status(401).build()
        }
        return ResponseEntity.ok(mapOf("inWishlist" to inWishlist))
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Удалить из избранного")
    fun remove(
        request: HttpServletRequest,
        @PathVariable productId: Long
    ): ResponseEntity<Void> {
        val email = request.email()
        val guestId = request.guestId()
        when {
            email != null -> wishlistService.removeFromWishlist(email, productId)
            guestId != null -> wishlistService.removeFromGuestWishlist(guestId, productId)
            else -> return ResponseEntity.status(401).build()
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/merge")
    @Operation(summary = "Перенести избранное гостя на аккаунт после логина")
    fun merge(
        request: HttpServletRequest,
        @RequestBody req: MergeWishlistRequest
    ): ResponseEntity<List<WishlistItemResponse>> {
        val email = request.email() ?: return ResponseEntity.status(401).build()
        wishlistService.mergeWishlist(email, req.guestId)
        return ResponseEntity.ok(wishlistService.getWishlist(email))
    }
}
