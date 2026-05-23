package com.shop.product.service

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(private val cloudinary: Cloudinary) {

    fun upload(file: MultipartFile): String {
        val result = cloudinary.uploader().upload(
            file.bytes,
            ObjectUtils.asMap(
                "folder", "shop/products",
                "resource_type", "image"
            )
        )
        return result["secure_url"] as String
    }
}