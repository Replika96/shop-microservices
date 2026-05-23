package com.shop.product.repository

import com.shop.product.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface ProductRepository : JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    fun findFirstByNameIgnoreCase(name: String): Optional<Product>
}
