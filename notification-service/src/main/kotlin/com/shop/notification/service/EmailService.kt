package com.shop.notification.service

import com.shop.notification.messaging.OrderEvent
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val from: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun sendOrderStatusEmail(event: OrderEvent) {
        val (subject, body) = buildEmail(event) ?: return
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            MimeMessageHelper(message, true, "UTF-8").apply {
                setFrom(from)
                setTo(event.userEmail)
                setSubject(subject)
                setText(body, true) // true = HTML
            }
            mailSender.send(message)
            log.info("Email sent to ${event.userEmail} for order #${event.orderId} status=${event.status}")
        } catch (e: Exception) {
            log.error("Failed to send email to ${event.userEmail}: ${e.message}")
        }
    }

    private fun buildEmail(event: OrderEvent): Pair<String, String>? {
        val orderId = event.orderId
        val statusInfo = when (event.status) {
            "PENDING"   -> "⏳ Принят" to "Ваш заказ получен и ожидает подтверждения."
            "CONFIRMED" -> "✅ Подтверждён" to "Ваш заказ подтверждён и готовится к отправке."
            "SHIPPED"   -> "🚚 Отправлен" to "Ваш заказ передан в службу доставки."
            "DELIVERED" -> "🎉 Доставлен" to "Ваш заказ доставлен. Спасибо за покупку!"
            "CANCELLED" -> "❌ Отменён" to "Ваш заказ был отменён."
            else        -> return null // Неизвестный статус — не шлём письмо
        }

        val (statusLabel, statusText) = statusInfo
        val subject = "Заказ #$orderId — $statusLabel"
        val html = """
            <!DOCTYPE html>
            <html lang="ru">
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px;">
              <div style="max-width: 520px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">

                <div style="background: #1e293b; padding: 24px 32px;">
                  <h1 style="color: white; margin: 0; font-size: 22px;">🛍️ DragonFlex Shop</h1>
                </div>

                <div style="padding: 32px;">
                  <h2 style="color: #1e293b; margin-top: 0;">Статус заказа обновлён</h2>

                  <div style="background: #f8fafc; border-radius: 8px; padding: 16px 20px; margin: 20px 0;">
                    <p style="margin: 0; color: #64748b; font-size: 14px;">Номер заказа</p>
                    <p style="margin: 4px 0 0; color: #1e293b; font-size: 20px; font-weight: bold;">#$orderId</p>
                  </div>

                  <div style="background: #f0fdf4; border-left: 4px solid #22c55e; border-radius: 0 8px 8px 0; padding: 16px 20px; margin: 20px 0;">
                    <p style="margin: 0; font-size: 16px; font-weight: bold; color: #166534;">$statusLabel</p>
                    <p style="margin: 6px 0 0; color: #15803d; font-size: 14px;">$statusText</p>
                  </div>

                  <p style="color: #64748b; font-size: 13px; margin-top: 32px;">
                    Если у вас есть вопросы, ответьте на это письмо.<br>
                    Спасибо, что выбрали нас!
                  </p>
                </div>

                <div style="background: #f8fafc; padding: 16px 32px; text-align: center;">
                  <p style="color: #94a3b8; font-size: 12px; margin: 0;">© 2026 DragonFlex Shop</p>
                </div>

              </div>
            </body>
            </html>
        """.trimIndent()

        return subject to html
    }
}
