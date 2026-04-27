package com.shop.user.security

import com.shop.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails =
        userRepository.findByEmail(email).map { user ->
            org.springframework.security.core.userdetails.User(
                user.email, user.password,
                listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
            )
        }.orElseThrow { UsernameNotFoundException("User not found: $email") }
}
