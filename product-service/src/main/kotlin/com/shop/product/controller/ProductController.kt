package com.shop.product.controller

import com.shop.product.model.Category
import com.shop.product.model.CreateProductRequest
import com.shop.product.model.PageResponse
import com.shop.product.model.ProductResponse
import com.shop.product.model.UpdateProductRequest
import com.shop.product.service.ImageService
import com.shop.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Каталог товаров с поиском и фильтрацией")
class ProductController(
    private val productService: ProductService,
    private val imageService: ImageService
) {

    private val allowedSortFields = setOf("id", "price", "name", "stock")

    @GetMapping
    @Operation(summary = "Поиск и фильтрация товаров")
    fun search(
        @Parameter(description = "Поиск по названию") @RequestParam(required = false) search: String?,
        @Parameter(description = "Фильтр по категории") @RequestParam(required = false) category: Category?,
        @Parameter(description = "Минимальная цена") @RequestParam(required = false) minPrice: BigDecimal?,
        @Parameter(description = "Максимальная цена") @RequestParam(required = false) maxPrice: BigDecimal?,
        @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Размер страницы (макс 100)") @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "Поле сортировки: id, price, name, stock") @RequestParam(defaultValue = "id") sortBy: String,
        @Parameter(description = "Направление: asc, desc") @RequestParam(defaultValue = "asc") sortDir: String
    ): ResponseEntity<PageResponse<ProductResponse>> {
        val direction = if (sortDir.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        val field = if (sortBy in allowedSortFields) sortBy else "id"
        val pageable = PageRequest.of(page, size.coerceIn(1, 100), Sort.by(direction, field))
        return ResponseEntity.ok(productService.search(search, category, minPrice, maxPrice, pageable))
    }

    @GetMapping("/categories")
    @Operation(summary = "Список всех категорий")
    fun getCategories(): ResponseEntity<List<Category>> =
        ResponseEntity.ok(productService.getCategories())

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    fun getById(@PathVariable id: Long): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.getById(id))

    @PostMapping
    @Operation(summary = "Добавить товар (admin)", security = [SecurityRequirement(name = "bearerAuth")])
    fun create(@Valid @RequestBody req: CreateProductRequest): ResponseEntity<ProductResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req))

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить товар (admin)", security = [SecurityRequirement(name = "bearerAuth")])
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: UpdateProductRequest
    ): ResponseEntity<ProductResponse> =
        ResponseEntity.ok(productService.update(id, req))

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар (admin)", security = [SecurityRequirement(name = "bearerAuth")])
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        productService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Загрузить изображение товара (admin)", security = [SecurityRequirement(name = "bearerAuth")])
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> =
        ResponseEntity.ok(mapOf("url" to imageService.upload(file)))
}
