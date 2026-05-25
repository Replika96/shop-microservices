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

    // ─── Вспомогательный метод ──────────────────────────────────────────────

    private fun buildResponse(items: List<CartItem>): CartResponse {
        val responses = items.map { it.toResponse() }
        return CartResponse(
            items = responses,
            totalItems = responses.sumOf { it.quantity },
            totalPrice = responses.fold(BigDecimal.ZERO) { acc, i -> acc.add(i.total) }
        )
    }

    // ─── Авторизованный пользователь ────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getCart(email: String): CartResponse =
        buildResponse(cartRepository.findByUserEmail(email))

    @Transactional
    fun addToCart(email: String, req: AddToCartRequest): CartResponse {
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
    fun clearCart(email: String) = cartRepository.deleteByUserEmail(email)

    // ─── Гость ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    fun getGuestCart(guestId: String): CartResponse =
        buildResponse(cartRepository.findByGuestId(guestId))

    @Transactional
    fun addToGuestCart(guestId: String, req: AddToCartRequest): CartResponse {
        val product = productClient.getById(req.productId)
        val existing = cartRepository.findByGuestIdAndProductId(guestId, req.productId)
        if (existing != null) {
            existing.quantity += req.quantity
            cartRepository.save(existing)
        } else {
            cartRepository.save(
                CartItem(
                    guestId = guestId,
                    productId = product.id,
                    productName = product.name,
                    price = product.price,
                    quantity = req.quantity,
                    imageUrl = product.imageUrl
                )
            )
        }
        return getGuestCart(guestId)
    }

    @Transactional
    fun updateGuestQuantity(guestId: String, itemId: Long, req: UpdateQuantityRequest): CartResponse {
        val item = cartRepository.findById(itemId)
            .orElseThrow { NoSuchElementException("Cart item $itemId not found") }
        require(item.guestId == guestId) { "Access denied" }
        item.quantity = req.quantity
        cartRepository.save(item)
        return getGuestCart(guestId)
    }

    @Transactional
    fun removeGuestItem(guestId: String, itemId: Long): CartResponse {
        val item = cartRepository.findById(itemId)
            .orElseThrow { NoSuchElementException("Cart item $itemId not found") }
        require(item.guestId == guestId) { "Access denied" }
        cartRepository.delete(item)
        return getGuestCart(guestId)
    }

    @Transactional
    fun clearGuestCart(guestId: String) = cartRepository.deleteByGuestId(guestId)

    // ─── Перенос корзины гостя на аккаунт ───────────────────────────────────

    @Transactional
    fun mergeCart(email: String, guestId: String): CartResponse {
        val guestItems = cartRepository.findByGuestId(guestId)
        guestItems.forEach { guestItem ->
            val existing = cartRepository.findByUserEmailAndProductId(email, guestItem.productId)
            if (existing != null) {
                existing.quantity += guestItem.quantity
                cartRepository.save(existing)
            } else {
                cartRepository.save(
                    CartItem(
                        userEmail = email,
                        productId = guestItem.productId,
                        productName = guestItem.productName,
                        price = guestItem.price,
                        quantity = guestItem.quantity,
                        imageUrl = guestItem.imageUrl
                    )
                )
            }
        }
        cartRepository.deleteByGuestId(guestId)
        return getCart(email)
    }
}
