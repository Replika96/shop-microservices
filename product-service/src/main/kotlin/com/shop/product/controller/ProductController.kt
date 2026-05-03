package com.shop.product.controller

import com.shop.product.model.*
import com.shop.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Каталог товаров с поиском и фильтрацией")
class ProductController(private val productService: ProductService) {

    @GetMapping
    @Operation(summary = "Поиск и фильтрация товаров")
    fun search(
        @Parameter(description = "Поиск по названию") @RequestParam(required = false) search: String?,
        @Parameter(description = "Фильтр по категории") @RequestParam(required = false) category: Category?,
        @Parameter(description = "Минимальная цена") @RequestParam(required = false) minPrice: BigDecimal?,
        @Parameter(description = "Максимальная цена") @RequestParam(required = false) maxPrice: BigDecimal?
    ): ResponseEntity<List<ProductResponse>> =
        ResponseEntity.ok(productService.search(search, category, minPrice, maxPrice))

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.getById(id))

    @PostMapping
    @Operation(summary = "Добавить товар (admin)")
    fun create(@Valid @RequestBody req: CreateProductRequest): ResponseEntity<ProductResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req))

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить товар (admin)")
    fun update(
        @PathVariable id: Long,
        @RequestBody req: UpdateProductRequest
    ): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.update(id, req))

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар (admin)")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        productService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
