package com.shop.product.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(private val cloudinary: Cloudinary) {

    companion object {
        private val ALLOWED_TYPES = setOf("image/jpeg", "image/png", "image/webp", "image/gif")
        private const val MAX_SIZE_BYTES = 5 * 1024 * 1024L // 5 MB
    }

    fun upload(file: MultipartFile): String {
        require(!file.isEmpty) { "File must not be empty" }
        require(file.size <= MAX_SIZE_BYTES) { "File size exceeds 5 MB limit" }
        require(file.contentType in ALLOWED_TYPES) { "Only JPEG, PNG, WebP and GIF images are allowed" }

        val result = cloudinary.uploader().upload(
            file.bytes,
            ObjectUtils.asMap("folder", "shop/products", "resource_type", "image")
        )
        return result["secure_url"] as String
    }
}
