package com.shop.user.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    var surname: String = "",
    var patronymic: String = "",
    var phone: String = "",

    // адрес доставки
    var city: String = "",
    var region: String = "",
    var street: String = "",
    var zipCode: String = "",

    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER
)

enum class Role { USER, ADMIN }
