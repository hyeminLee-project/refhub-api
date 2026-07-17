package com.refhub.api.domain.user

import com.refhub.api.common.exception.DuplicateException
import com.refhub.api.common.exception.NotFoundException
import com.refhub.api.domain.user.dto.SignupRequest
import com.refhub.api.domain.user.dto.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signup(request: SignupRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateException("이메일")
        }

        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                nickname = request.nickname,
            )
        )

        return UserResponse(id = user.id, email = user.email, nickname = user.nickname)
    }

    fun findByEmail(email: String): User =
        userRepository.findByEmail(email)
            ?: throw NotFoundException("사용자", email)

    fun findById(id: Long): User =
        userRepository.findById(id).orElseThrow { NotFoundException("사용자", id) }
}
