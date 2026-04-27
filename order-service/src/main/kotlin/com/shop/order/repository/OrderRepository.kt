package com.shop.order.repository

import com.shop.order.model.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserEmail(email: String): List<Order>
}
