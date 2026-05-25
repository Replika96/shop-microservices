package com.shop.cart.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "cart_items")
class CartItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // ровно одно из двух полей не null: userEmail (авторизованный) или guestId (гость)
    val userEmail: String? = null,
    val guestId: String? = null,

    @Column(nullable = false) val productId: Long,
    @Column(nullable = false) val productName: String,
    @Column(nullable = false) val price: BigDecimal,
    @Column(nullable = false) var quantity: Int = 1,
    val imageUrl: String = ""
)
