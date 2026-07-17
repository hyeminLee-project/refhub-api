package com.refhub.api.security

import com.refhub.api.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val userId: Long,
    val email: String,
    private val pwd: String,
    private val authorities: Collection<GrantedAuthority>,
) : UserDetails {

    override fun getAuthorities() = authorities
    override fun getPassword() = pwd
    override fun getUsername() = email

    companion object {
        fun from(user: User) = UserPrincipal(
            userId = user.id,
            email = user.email,
            pwd = user.password,
            authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}")),
        )
    }
}
