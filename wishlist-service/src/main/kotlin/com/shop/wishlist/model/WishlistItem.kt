package com.shop.wishlist.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "wishlist_items")
class WishlistItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // ровно одно из двух полей не null: userEmail (авторизованный) или guestId (гость)
    val userEmail: String? = null,
    val guestId: String? = null,

    @Column(nullable = false) val productId: Long,
    @Column(nullable = false) val productName: String,
    @Column(nullable = false) val price: BigDecimal,
    val imageUrl: String = ""
)
