package com.shop.notification.messaging

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class OrderEventConsumer(private val messagingTemplate: SimpMessagingTemplate) {

    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["order.events"])
    fun onOrderEvent(event: OrderEvent) {
        log.info("Received order event: $event")
        // Broadcast to /topic/orders/{orderId}
        messagingTemplate.convertAndSend("/topic/orders/${event.orderId}", event)
        // Also send to user-specific channel
        messagingTemplate.convertAndSend("/topic/users/${event.userEmail}/orders", event)
    }
}
