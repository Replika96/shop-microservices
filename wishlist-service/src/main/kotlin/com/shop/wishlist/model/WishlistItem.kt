package com.shop.wishlist.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "wishlist_items", uniqueConstraints = [
    UniqueConstraint(columnNames = ["userEmail", "productId"])
])
data class WishlistItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false) val userEmail: String,
    @Column(nullable = false) val productId: Long,
    @Column(nullable = false) val productName: String,
    @Column(nullable = false) val price: BigDecimal,
    val imageUrl: String = ""
)
