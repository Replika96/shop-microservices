package com.shop.product.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false) var name: String,
    @Column(length = 2000) var description: String = "",
    @Column(nullable = false) var price: BigDecimal,
    @Column(nullable = false) var stock: Int = 0,
    @Enumerated(EnumType.STRING) var category: Category = Category.OTHER,
    var imageUrl: String = ""
)

enum class Category {
    BATH,        // Бани и чаны
    GRILL,       // Мангалы и барбекю
    FURNITURE,   // Мебель
    GARDEN,      // Сад и огород
    TOOLS,       // Инструменты
    OTHER
}
