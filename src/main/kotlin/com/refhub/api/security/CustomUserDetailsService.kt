package com.refhub.api.security

import com.refhub.api.domain.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $email")
        return UserPrincipal.from(user)
    }

    fun loadUserById(id: Long): UserPrincipal {
        val user = userRepository.findById(id)
            .orElseThrow { UsernameNotFoundException("사용자를 찾을 수 없습니다: $id") }
        return UserPrincipal.from(user)
    }
}
