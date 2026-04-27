package com.shop.product.messaging

import com.shop.product.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

// Когда создаётся заказ — уменьшаем stock товара
data class OrderCreatedEvent(
    val orderId: Long = 0,
    val productName: String = "",
    val quantity: Int = 0
)

@Service
class OrderEventConsumer(private val productService: ProductService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["order.events"])
    fun onOrderEvent(event: OrderCreatedEvent) {
        log.info("Order event received: orderId=${event.orderId}, product=${event.productName}")
        // уменьшаем stock если товар найден по имени
        productService.decreaseStockByName(event.productName, event.quantity)
    }
}
