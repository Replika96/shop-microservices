package com.shop.order.service

import com.shop.order.client.CartClient
import com.shop.order.messaging.OrderEventPublisher
import com.shop.order.model.*
import com.shop.order.repository.OrderRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartClient: CartClient,
    private val publisher: OrderEventPublisher
) {

    @Transactional
    fun create(email: String, req: CreateOrderRequest, token: String): OrderResponse {
        val cart = cartClient.getCart(token)
        require(cart.items.isNotEmpty()) { "Cart is empty" }

        val order = Order(
            userEmail = email,
            totalPrice = cart.totalPrice,
            address = req.address
        )
        cart.items.forEach { item ->
            order.items.add(
                OrderItem(
                    order = order,
                    productId = item.productId,
                    productName = item.productName,
                    price = item.price,
                    quantity = item.quantity,
                    imageUrl = item.imageUrl
                )
            )
        }

        val saved = orderRepository.save(order)
        cartClient.clearCart(token)

        publisher.publish(
            OrderEvent(
                orderId = saved.id,
                userEmail = email,
                status = saved.status.name,
                items = saved.items.map { OrderEventItem(it.productName, it.quantity) }
            )
        )

        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): OrderResponse =
        orderRepository.findByIdWithItems(id)
            .map { it.toResponse() }
            .orElseThrow { NoSuchElementException("Order $id not found") }

    @Transactional(readOnly = true)
    fun getByUser(email: String, page: Int, size: Int): PageResponse<OrderResponse> {
        val result = orderRepository.findByUserEmailOrderByCreatedAtDesc(
            email, PageRequest.of(page, size.coerceIn(1, 50))
        )
        return PageResponse(
            content = result.content.map { it.toResponse() },
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            page = page,
            size = result.size
        )
    }

    @Transactional(readOnly = true)
    fun getAll(page: Int, size: Int): PageResponse<OrderResponse> {
        val result = orderRepository.findAllByOrderByCreatedAtDesc(
            PageRequest.of(page, size.coerceIn(1, 100))
        )
        return PageResponse(
            content = result.content.map { it.toResponse() },
            totalElements = result.totalElements,
            totalPages = result.totalPages,
            page = page,
            size = result.size
        )
    }

    @Transactional
    fun updateStatus(id: Long, req: UpdateStatusRequest): OrderResponse {
        val order = orderRepository.findByIdWithItems(id)
            .orElseThrow { NoSuchElementException("Order $id not found") }
        order.status = req.status
        val saved = orderRepository.save(order)
        publisher.publish(
            OrderEvent(
                orderId = saved.id,
                userEmail = saved.userEmail,
                status = saved.status.name,
                items = saved.items.map { OrderEventItem(it.productName, it.quantity) }
            )
        )
        return saved.toResponse()
    }
}
