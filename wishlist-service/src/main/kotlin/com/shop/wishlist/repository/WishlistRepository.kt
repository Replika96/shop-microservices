package com.shop.wishlist.repository

import com.shop.wishlist.model.WishlistItem
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<WishlistItem, Long> {
    fun findByUserEmail(email: String): List<WishlistItem>
    fun findByUserEmailAndProductId(email: String, productId: Long): WishlistItem?
    fun deleteByUserEmailAndProductId(email: String, productId: Long)
    fun existsByUserEmailAndProductId(email: String, productId: Long): Boolean
}
