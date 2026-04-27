package com.shop.product.repository

import com.shop.product.model.Category
import com.shop.product.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal

interface ProductRepository : JpaRepository<Product, Long> {

    // поиск по названию (без учёта регистра)
    fun findByNameContainingIgnoreCase(name: String): List<Product>

    // фильтр по категории
    fun findByCategory(category: Category): List<Product>

    // комбинированный поиск с фильтрацией
    @Query("""
    SELECT p FROM Product p WHERE
    (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
    AND (:#{#category?.name()} IS NULL OR p.category = :category)
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    fun search(
        @Param("search") search: String?,
        @Param("category") category: Category?,
        @Param("minPrice") minPrice: BigDecimal?,
        @Param("maxPrice") maxPrice: BigDecimal?
    ): List<Product>
}
