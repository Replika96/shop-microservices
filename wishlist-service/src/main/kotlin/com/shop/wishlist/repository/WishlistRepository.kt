package com.shop.wishlist.repository

import com.shop.wishlist.model.WishlistItem
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<WishlistItem, Long> {
    fun findByUserEmail(email: String): List<WishlistItem>
    fun findByUserEmailAndProductId(email: String, productId: Long): WishlistItem?
    fun existsByUserEmailAndProductId(email: String, productId: Long): Boolean
    fun deleteByUserEmailAndProductId(email: String, productId: Long)

    fun findByGuestId(guestId: String): List<WishlistItem>
    fun findByGuestIdAndProductId(guestId: String, productId: Long): WishlistItem?
    fun existsByGuestIdAndProductId(guestId: String, productId: Long): Boolean
    fun deleteByGuestIdAndProductId(guestId: String, productId: Long)
    fun deleteByGuestId(guestId: String)
}
