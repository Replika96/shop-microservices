package com.shop.order.messaging

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val ORDER_EXCHANGE = "order.exchange"
const val ORDER_QUEUE = "order.events"
const val ORDER_ROUTING_KEY = "order.status"

@Configuration
class RabbitConfig {
    @Bean fun orderExchange(): TopicExchange = TopicExchange(ORDER_EXCHANGE)
    @Bean fun orderQueue(): Queue = Queue(ORDER_QUEUE, true)
    @Bean fun binding(): Binding = BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING_KEY)
    @Bean fun messageConverter() = Jackson2JsonMessageConverter()
    @Bean fun rabbitTemplate(cf: ConnectionFactory, conv: Jackson2JsonMessageConverter): RabbitTemplate =
        RabbitTemplate(cf).also { it.messageConverter = conv }
}
