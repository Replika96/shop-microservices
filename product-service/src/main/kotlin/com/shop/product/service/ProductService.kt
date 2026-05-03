package com.shop.product.service

import com.shop.product.model.*
import com.shop.product.repository.ProductRepository
import com.shop.product.repository.ProductSpecification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun create(req: CreateProductRequest): ProductResponse {
        val product = Product(
            name = req.name, description = req.description,
            price = req.price, stock = req.stock,
            category = req.category, imageUrl = req.imageUrl
        )
        return productRepository.save(product).toResponse()
    }

    fun getById(id: Long): ProductResponse =
        productRepository.findById(id)
            .map { it.toResponse() }
            .orElseThrow { NoSuchElementException("Product $id not found") }

    fun getAll(): List<ProductResponse> =
        productRepository.findAll().map { it.toResponse() }

    fun search(
        search: String?, category: Category?,
        minPrice: BigDecimal?, maxPrice: BigDecimal?
    ): List<ProductResponse> =
        productRepository.findAll(
            ProductSpecification.filter(search, category, minPrice, maxPrice)
        ).map { it.toResponse() }

    @Transactional
    fun update(id: Long, req: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product $id not found") }
        req.name?.let { product.name = it }
        req.description?.let { product.description = it }
        req.price?.let { product.price = it }
        req.stock?.let { product.stock = it }
        req.category?.let { product.category = it }
        req.imageUrl?.let { product.imageUrl = it }
        return productRepository.save(product).toResponse()
    }

    fun delete(id: Long) {
        if (!productRepository.existsById(id)) throw NoSuchElementException("Product $id not found")
        productRepository.deleteById(id)
    }

    @Transactional
    fun decreaseStockByName(productName: String, quantity: Int) {
        val products = productRepository.findByNameContainingIgnoreCase(productName)
        products.firstOrNull()?.let { product ->
            if (product.stock >= quantity) {
                product.stock -= quantity
                productRepository.save(product)
            }
        }
    }
}
