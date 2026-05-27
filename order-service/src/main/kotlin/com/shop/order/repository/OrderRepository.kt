package com.shop.order.repository

import com.shop.order.model.Order
import com.shop.order.model.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long> {

    fun findByUserEmailOrderByCreatedAtDesc(email: String, pageable: Pageable): Page<Order>

    fun findByUserEmailAndStatusOrderByCreatedAtDesc(
        email: String, status: OrderStatus, pageable: Pageable
    ): Page<Order>

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Order>

    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    fun findByIdWithItems(id: Long): Optional<Order>
}
