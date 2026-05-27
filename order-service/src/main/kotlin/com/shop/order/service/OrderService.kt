package com.shop.order.service

import com.shop.order.client.CartClient
import com.shop.order.client.ProductClient
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
    private val productClient: ProductClient,
    private val publisher: OrderEventPublisher
) {

    @Transactional
    fun create(email: String, req: CreateOrderRequest, token: String): OrderResponse {
        val cart = cartClient.getCart(token)
        require(cart.items.isNotEmpty()) { "Cart is empty" }

        // Проверяем наличие товаров перед созданием заказа
        cart.items.forEach { item ->
            val product = productClient.getById(item.productId)
            require(product.stock >= item.quantity) {
                "Недостаточно товара '${item.productName}': в наличии ${product.stock}, запрошено ${item.quantity}"
            }
        }

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

    @Transactional
    fun cancel(email: String, orderId: Long): OrderResponse {
        val order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow { NoSuchElementException("Order $orderId not found") }
        require(order.userEmail == email) { "Access denied" }
        require(order.status == OrderStatus.PENDING) { "Отменить можно только заказ в статусе PENDING" }

        order.status = OrderStatus.CANCELLED
        val saved = orderRepository.save(order)

        // Публикуем событие — product-service восстановит остатки
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

    @Transactional(readOnly = true)
    fun getById(id: Long): OrderResponse =
        orderRepository.findByIdWithItems(id)
            .map { it.toResponse() }
            .orElseThrow { NoSuchElementException("Order $id not found") }

    @Transactional(readOnly = true)
    fun getByUser(email: String, page: Int, size: Int, status: OrderStatus?): PageResponse<OrderResponse> {
        val pageable = PageRequest.of(page, size.coerceIn(1, 50))
        val result = if (status != null)
            orderRepository.findByUserEmailAndStatusOrderByCreatedAtDesc(email, status, pageable)
        else
            orderRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable)
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
