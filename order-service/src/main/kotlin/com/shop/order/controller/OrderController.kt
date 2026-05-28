package com.shop.order.controller

import com.shop.order.model.*
import com.shop.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management API")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @Operation(summary = "Оформить заказ из корзины")
    fun create(
        request: HttpServletRequest,
        @Valid @RequestBody req: CreateOrderRequest
    ): ResponseEntity<OrderResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        val token = request.getHeader("Authorization")
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(email, req, token))
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Отменить заказ (только PENDING)")
    fun cancel(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<OrderResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(orderService.cancel(email, id))
    }

    @GetMapping("/my")
    @Operation(summary = "Мои заказы (с пагинацией и фильтром по статусу)")
    fun getMyOrders(
        request: HttpServletRequest,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<PageResponse<OrderResponse>> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        val orderStatus = status?.let { runCatching { OrderStatus.valueOf(it) }.getOrNull() }
        return ResponseEntity.ok(orderService.getByUser(email, page, size, orderStatus))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Заказ по ID")
    fun getById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.getById(id))

    @GetMapping
    @Operation(summary = "Все заказы (admin)")
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<PageResponse<OrderResponse>> =
        ResponseEntity.ok(orderService.getAll(page, size))

    @PatchMapping("/{id}/status")
    @Operation(summary = "Обновить статус (admin)")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody req: UpdateStatusRequest
    ): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.updateStatus(id, req))
}
