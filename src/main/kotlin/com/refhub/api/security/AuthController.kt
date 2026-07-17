package com.refhub.api.security

import com.refhub.api.common.response.ApiResponse
import com.refhub.api.domain.user.UserService
import com.refhub.api.domain.user.dto.LoginRequest
import com.refhub.api.domain.user.dto.SignupRequest
import com.refhub.api.domain.user.dto.TokenResponse
import com.refhub.api.domain.user.dto.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService,
) {
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody request: SignupRequest): ApiResponse<UserResponse> =
        ApiResponse.ok(userService.signup(request))

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<TokenResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val principal = authentication.principal as UserPrincipal
        val token = jwtTokenProvider.generateToken(principal.userId, principal.email)
        return ApiResponse.ok(TokenResponse(accessToken = token))
    }
}
