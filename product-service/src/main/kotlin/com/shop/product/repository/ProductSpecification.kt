package com.shop.product.repository

import com.shop.product.model.Category
import com.shop.product.model.Product
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal

object ProductSpecification {
    fun filter(
        search: String?,
        category: Category?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?
    ): Specification<Product> = Specification { root, _, cb ->
        val predicates = mutableListOf<Predicate>()
        search?.let {
            predicates.add(cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%"))
        }
        category?.let {
            predicates.add(cb.equal(root.get<Category>("category"), it))
        }
        minPrice?.let {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), it))
        }
        maxPrice?.let {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), it))
        }
        cb.and(*predicates.toTypedArray())
    }
}