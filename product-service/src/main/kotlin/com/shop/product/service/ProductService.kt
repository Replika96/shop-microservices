package com.shop.product.service

import com.shop.product.model.*
import com.shop.product.repository.ProductRepository
import com.shop.product.repository.ProductSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductService(private val productRepository: ProductRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun create(req: CreateProductRequest): ProductResponse {
        val product = Product(
            name = req.name, description = req.description,
            price = req.price, stock = req.stock,
            category = req.category, imageUrl = req.imageUrl
        )
        return productRepository.save(product).toResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ProductResponse =
        productRepository.findById(id)
            .map { it.toResponse() }
            .orElseThrow { NoSuchElementException("Product $id not found") }

    @Transactional(readOnly = true)
    fun search(
        search: String?, category: Category?,
        minPrice: BigDecimal?, maxPrice: BigDecimal?,
        pageable: Pageable
    ): PageResponse<ProductResponse> {
        val page = productRepository.findAll(
            ProductSpecification.filter(search, category, minPrice, maxPrice),
            pageable
        )
        return PageResponse(
            content = page.content.map { it.toResponse() },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            page = pageable.pageNumber,
            size = pageable.pageSize
        )
    }

    @Transactional(readOnly = true)
    fun getCategories(): List<Category> = Category.entries

    @Transactional
    fun update(id: Long, req: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product $id not found") }
        req.name?.let {
            require(it.isNotBlank()) { "name must not be blank" }
            product.name = it
        }
        req.description?.let { product.description = it }
        req.price?.let { product.price = it }
        req.stock?.let { product.stock = it }
        req.category?.let { product.category = it }
        req.imageUrl?.let { product.imageUrl = it }
        return product.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!productRepository.existsById(id)) throw NoSuchElementException("Product $id not found")
        productRepository.deleteById(id)
    }

    // Уменьшить остаток (вызывается из RabbitMQ consumer при создании заказа)
    // @Version обеспечивает оптимистичную блокировку — при конфликте сообщение будет повторно обработано
    @Transactional
    fun decreaseStock(productName: String, quantity: Int) {
        val product = productRepository.findFirstByNameIgnoreCase(productName).orElse(null)
        if (product == null) {
            log.warn("Product not found by name '{}', stock not decreased", productName)
            return
        }
        if (product.stock < quantity) {
            log.warn("Insufficient stock for '{}': have {}, need {}", productName, product.stock, quantity)
            return
        }
        product.stock -= quantity
        productRepository.save(product)
    }

    // Восстановить остаток (вызывается при отмене заказа)
    @Transactional
    fun increaseStock(productName: String, quantity: Int) {
        val product = productRepository.findFirstByNameIgnoreCase(productName).orElse(null)
        if (product == null) {
            log.warn("Product not found by name '{}', stock not restored", productName)
            return
        }
        product.stock += quantity
        productRepository.save(product)
        log.info("Stock restored for '{}': +{}, now {}", productName, quantity, product.stock)
    }
}
