package com.shop.order.controller

import com.shop.order.model.CreateOrderRequest
import com.shop.order.model.OrderResponse
import com.shop.order.model.UpdateStatusRequest
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
    @Operation(summary = "Create a new order")
    fun create(
        request: HttpServletRequest,
        @Valid @RequestBody req: CreateOrderRequest
    ): ResponseEntity<OrderResponse> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(email, req))
    }

    @GetMapping("/my")
    @Operation(summary = "Get orders for current user")
    fun getMyOrders(request: HttpServletRequest): ResponseEntity<List<OrderResponse>> {
        val email = request.getAttribute("userEmail") as? String
            ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(orderService.getByUser(email))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    fun getById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.getById(id))

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
