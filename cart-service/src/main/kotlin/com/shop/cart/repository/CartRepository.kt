package com.shop.cart.repository

import com.shop.cart.model.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartRepository : JpaRepository<CartItem, Long> {
    fun findByUserEmail(email: String): List<CartItem>
    fun findByUserEmailAndProductId(email: String, productId: Long): CartItem?
    fun deleteByUserEmail(email: String)

    fun findByGuestId(guestId: String): List<CartItem>
    fun findByGuestIdAndProductId(guestId: String, productId: Long): CartItem?
    fun deleteByGuestId(guestId: String)
}
