package com.shop.order.repository

import com.shop.order.model.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long> {

    // Пагинация без @EntityGraph — items грузятся батчем (@BatchSize в Order)
    fun findByUserEmailOrderByCreatedAtDesc(email: String, pageable: Pageable): Page<Order>

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Order>

    // Одиночные запросы — @EntityGraph безопасен (нет pagination)
    @EntityGraph(attributePaths = ["items"])
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    fun findByIdWithItems(id: Long): Optional<Order>
}
