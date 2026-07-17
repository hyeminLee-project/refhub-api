package com.refhub.api.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private val provider = JwtTokenProvider(
        secret = "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha",
        expirationMs = 3600000,
    )

    @Test
    fun `generate and validate token`() {
        val token = provider.generateToken(userId = 1L, email = "test@example.com")

        assertThat(provider.validateToken(token)).isTrue()
        assertThat(provider.getUserIdFromToken(token)).isEqualTo(1L)
    }

    @Test
    fun `invalid token returns false`() {
        assertThat(provider.validateToken("invalid.token.here")).isFalse()
    }
}
