package com.shop.order.service

import com.shop.order.messaging.OrderEventPublisher
import com.shop.order.model.*
import com.shop.order.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val publisher: OrderEventPublisher
) {
    fun create(email: String, req: CreateOrderRequest): OrderResponse {
        val order = orderRepository.save(
            Order(userEmail = email, productName = req.productName, quantity = req.quantity, price = req.price)
        )
        publisher.publish(OrderEvent(order.id, email, order.status.name, order.productName))
        return order.toResponse()
    }

    fun getById(id: Long): OrderResponse =
        orderRepository.findById(id).map { it.toResponse() }.orElseThrow { NoSuchElementException("Order $id not found") }

    fun getByUser(email: String): List<OrderResponse> =
        orderRepository.findByUserEmail(email).map { it.toResponse() }

    fun getAll(): List<OrderResponse> = orderRepository.findAll().map { it.toResponse() }

    @Transactional
    fun updateStatus(id: Long, req: UpdateStatusRequest): OrderResponse {
        val order = orderRepository.findById(id).orElseThrow { NoSuchElementException("Order $id not found") }
        order.status = req.status
        val saved = orderRepository.save(order)
        publisher.publish(OrderEvent(saved.id, saved.userEmail, saved.status.name, saved.productName))
        return saved.toResponse()
    }
}
