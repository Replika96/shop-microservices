package com.shop.product.messaging

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean fun orderExchange(): TopicExchange = TopicExchange("order.exchange")
    @Bean fun orderQueue(): Queue = Queue("order.events", true)
    @Bean fun binding(): Binding = BindingBuilder.bind(orderQueue()).to(orderExchange()).with("order.status")
    @Bean fun messageConverter() = Jackson2JsonMessageConverter()
    @Bean fun rabbitTemplate(cf: ConnectionFactory, conv: Jackson2JsonMessageConverter): RabbitTemplate =
        RabbitTemplate(cf).also { it.messageConverter = conv }
}
