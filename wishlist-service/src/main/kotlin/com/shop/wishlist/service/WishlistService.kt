package com.shop.wishlist.service

import com.shop.wishlist.client.ProductClient
import com.shop.wishlist.model.*
import com.shop.wishlist.repository.WishlistRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val productClient: ProductClient
) {

    // ─── Авторизованный пользователь ────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getWishlist(email: String): List<WishlistItemResponse> =
        wishlistRepository.findByUserEmail(email).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun isInWishlist(email: String, productId: Long): Boolean =
        wishlistRepository.existsByUserEmailAndProductId(email, productId)

    @Transactional
    fun addToWishlist(email: String, req: AddToWishlistRequest): WishlistItemResponse {
        val existing = wishlistRepository.findByUserEmailAndProductId(email, req.productId)
        if (existing != null) return existing.toResponse()
        val product = productClient.getById(req.productId)
        return wishlistRepository.save(
            WishlistItem(
                userEmail = email,
                productId = product.id,
                productName = product.name,
                price = product.price,
                imageUrl = product.imageUrl
            )
        ).toResponse()
    }

    @Transactional
    fun removeFromWishlist(email: String, productId: Long) =
        wishlistRepository.deleteByUserEmailAndProductId(email, productId)

    @Transactional
    fun toggle(email: String, req: AddToWishlistRequest): Map<String, Any> =
        if (isInWishlist(email, req.productId)) {
            removeFromWishlist(email, req.productId)
            mapOf("inWishlist" to false, "message" to "Удалено из избранного")
        } else {
            val item = addToWishlist(email, req)
            mapOf("inWishlist" to true, "message" to "Добавлено в избранное", "item" to item)
        }

    // ─── Гость ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getGuestWishlist(guestId: String): List<WishlistItemResponse> =
        wishlistRepository.findByGuestId(guestId).map { it.toResponse() }

    @Transactional(readOnly = true)
    fun isInGuestWishlist(guestId: String, productId: Long): Boolean =
        wishlistRepository.existsByGuestIdAndProductId(guestId, productId)

    @Transactional
    fun addToGuestWishlist(guestId: String, req: AddToWishlistRequest): WishlistItemResponse {
        val existing = wishlistRepository.findByGuestIdAndProductId(guestId, req.productId)
        if (existing != null) return existing.toResponse()
        val product = productClient.getById(req.productId)
        return wishlistRepository.save(
            WishlistItem(
                guestId = guestId,
                productId = product.id,
                productName = product.name,
                price = product.price,
                imageUrl = product.imageUrl
            )
        ).toResponse()
    }

    @Transactional
    fun removeFromGuestWishlist(guestId: String, productId: Long) =
        wishlistRepository.deleteByGuestIdAndProductId(guestId, productId)

    @Transactional
    fun toggleGuest(guestId: String, req: AddToWishlistRequest): Map<String, Any> =
        if (isInGuestWishlist(guestId, req.productId)) {
            removeFromGuestWishlist(guestId, req.productId)
            mapOf("inWishlist" to false, "message" to "Удалено из избранного")
        } else {
            val item = addToGuestWishlist(guestId, req)
            mapOf("inWishlist" to true, "message" to "Добавлено в избранное", "item" to item)
        }

    // ─── Перенос избранного гостя на аккаунт ────────────────────────────────

    @Transactional
    fun mergeWishlist(email: String, guestId: String) {
        val guestItems = wishlistRepository.findByGuestId(guestId)
        guestItems.forEach { guestItem ->
            val alreadyExists = wishlistRepository.existsByUserEmailAndProductId(email, guestItem.productId)
            if (!alreadyExists) {
                wishlistRepository.save(
                    WishlistItem(
                        userEmail = email,
                        productId = guestItem.productId,
                        productName = guestItem.productName,
                        price = guestItem.price,
                        imageUrl = guestItem.imageUrl
                    )
                )
            }
        }
        wishlistRepository.deleteByGuestId(guestId)
    }
}
