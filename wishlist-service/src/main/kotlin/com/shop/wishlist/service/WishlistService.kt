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

        // Данные товара — только от product-service, не от клиента
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
    fun removeFromWishlist(email: String, productId: Long) {
        wishlistRepository.deleteByUserEmailAndProductId(email, productId)
    }

    @Transactional
    fun toggle(email: String, req: AddToWishlistRequest): Map<String, Any> {
        return if (isInWishlist(email, req.productId)) {
            removeFromWishlist(email, req.productId)
            mapOf("inWishlist" to false, "message" to "Удалено из избранного")
        } else {
            val item = addToWishlist(email, req)
            mapOf("inWishlist" to true, "message" to "Добавлено в избранное", "item" to item)
        }
    }
}
