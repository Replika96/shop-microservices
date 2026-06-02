package com.shop.notification.messaging

import com.shop.notification.service.EmailService
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class OrderEventConsumer(
    private val messagingTemplate: SimpMessagingTemplate,
    private val emailService: EmailService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["order.events"])
    fun onOrderEvent(event: OrderEvent) {
        log.info("Received order event: $event")

        // WebSocket — для онлайн пользователей
        messagingTemplate.convertAndSend("/topic/orders/${event.orderId}", event)
        messagingTemplate.convertAndSend("/topic/users/${event.userEmail}/orders", event)

        // Email — всегда, независимо от онлайн-статуса
        emailService.sendOrderStatusEmail(event)
    }
}
