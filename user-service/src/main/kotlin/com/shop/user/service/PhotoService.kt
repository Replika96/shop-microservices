package com.shop.user.service

import com.cloudinary.Cloudinary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class PhotoService(private val cloudinary: Cloudinary) {

    private val allowedTypes = setOf("image/jpeg", "image/png", "image/webp")
    private val maxSizeBytes = 3 * 1024 * 1024L // 3 MB

    fun upload(file: MultipartFile): String {
        require(!file.isEmpty) { "File is empty" }
        require(file.size <= maxSizeBytes) { "File too large (max 3 MB)" }
        require(file.contentType in allowedTypes) { "Unsupported file type: ${file.contentType}" }

        return try {
            val result = cloudinary.uploader().upload(
                file.bytes,
                mapOf("folder" to "shop/profiles", "resource_type" to "image")
            )
            result["secure_url"] as String
        } catch (e: IOException) {
            throw IllegalStateException("Failed to upload photo: ${e.message}")
        }
    }
}
