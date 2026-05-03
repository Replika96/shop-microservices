package com.shop.product.repository

import com.shop.product.model.Category
import com.shop.product.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProductRepository : JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    fun findByNameContainingIgnoreCase(name: String): List<Product>
}