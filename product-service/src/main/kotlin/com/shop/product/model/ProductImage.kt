package com.shop.product.model

import jakarta.persistence.*

@Entity
@Table(name = "product_images")
class ProductImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var url: String,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0
)
