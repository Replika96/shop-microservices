package com.shop.order.controller

import com.shop.order.model.*
import com.shop.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management API")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    @Operation(summary = "Create a new order")
    fun create(
        @Parameter(description = "User email from JWT") @RequestHeader("X-User-Email") email: String,
        @Valid @RequestBody req: CreateOrderRequest
    ): ResponseEntity<OrderResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(email, req))

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    fun getById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.getById(id))

    @GetMapping("/my")
    @Operation(summary = "Get orders for current user")
    fun getMyOrders(
        @Parameter(description = "User email from JWT") @RequestHeader("X-User-Email") email: String
    ): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(orderService.getByUser(email))

    @GetMapping
    @Operation(summary = "Get all orders (admin)")
    fun getAll(): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(orderService.getAll())

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody req: UpdateStatusRequest
    ): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.updateStatus(id, req))
}
