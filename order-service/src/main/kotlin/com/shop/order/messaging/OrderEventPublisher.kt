package com.shop.order.messaging

import com.shop.order.model.OrderEvent
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class OrderEventPublisher(private val rabbitTemplate: RabbitTemplate) {
    fun publish(event: OrderEvent) {
        rabbitTemplate.convertAndSend(ORDER_EXCHANGE, ORDER_ROUTING_KEY, event)
    }
}
