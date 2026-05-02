package com.shop.cart.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "cart_items")
data class CartItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false) val userEmail: String,
    @Column(nullable = false) val productId: Long,
    @Column(nullable = false) val productName: String,
    @Column(nullable = false) val price: BigDecimal,
    @Column(nullable = false) var quantity: Int = 1,
    val imageUrl: String = ""
)
