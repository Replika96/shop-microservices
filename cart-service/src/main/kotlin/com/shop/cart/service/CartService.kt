package com.shop.cart.service

import com.shop.cart.client.ProductClient
import com.shop.cart.model.*
import com.shop.cart.repository.CartRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productClient: ProductClient
) {

    @Transactional(readOnly = true)
    fun getCart(email: String): CartResponse {
        val items = cartRepository.findByUserEmail(email).map { it.toResponse() }
        return CartResponse(
            items = items,
            totalItems = items.sumOf { it.quantity },
            totalPrice = items.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.total) }
        )
    }

    @Transactional
    fun addToCart(email: String, req: AddToCartRequest): CartResponse {
        // Цена и данные товара — только от product-service, не от клиента
        val product = productClient.getById(req.productId)

        val existing = cartRepository.findByUserEmailAndProductId(email, req.productId)
        if (existing != null) {
            existing.quantity += req.quantity
            cartRepository.save(existing)
        } else {
            cartRepository.save(
                CartItem(
                    userEmail = email,
                    productId = product.id,
                    productName = product.name,
                    price = product.price,
                    quantity = req.quantity,
                    imageUrl = product.imageUrl
                )
            )
        }
        return getCart(email)
    }

    @Transactional
    fun updateQuantity(email: String, itemId: Long, req: UpdateQuantityRequest): CartResponse {
        val item = cartRepository.findById(itemId)
            .orElseThrow { NoSuchElementException("Cart item $itemId not found") }
        require(item.userEmail == email) { "Access denied" }
        item.quantity = req.quantity
        cartRepository.save(item)
        return getCart(email)
    }

    @Transactional
    fun removeItem(email: String, itemId: Long): CartResponse {
        val item = cartRepository.findById(itemId)
            .orElseThrow { NoSuchElementException("Cart item $itemId not found") }
        require(item.userEmail == email) { "Access denied" }
        cartRepository.delete(item)
        return getCart(email)
    }

    @Transactional
    fun clearCart(email: String) {
        cartRepository.deleteByUserEmail(email)
    }
}
