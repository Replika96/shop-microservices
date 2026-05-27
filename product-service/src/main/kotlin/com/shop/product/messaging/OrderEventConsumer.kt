package com.shop.product.messaging

import com.shop.product.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

data class OrderCreatedEventItem(
    val productName: String = "",
    val quantity: Int = 0
)

data class OrderCreatedEvent(
    val orderId: Long = 0,
    val userEmail: String = "",
    val status: String = "",
    val items: List<OrderCreatedEventItem> = emptyList()
)

@Service
class OrderEventConsumer(private val productService: ProductService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["order.events"])
    fun onOrderEvent(event: OrderCreatedEvent) {
        log.info("Order event: orderId={}, status={}, items={}", event.orderId, event.status, event.items.size)
        when (event.status) {
            "PENDING" -> event.items.forEach { item ->
                if (item.quantity > 0) productService.decreaseStock(item.productName, item.quantity)
            }
            "CANCELLED" -> event.items.forEach { item ->
                if (item.quantity > 0) productService.increaseStock(item.productName, item.quantity)
            }
        }
    }
}
