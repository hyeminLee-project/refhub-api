package com.refhub.api.common.exception

import com.refhub.api.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(e.status)
            .body(ApiResponse.error(e.code, e.message ?: "요청을 처리할 수 없습니다."))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", message))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(e: BadCredentialsException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("AUTH_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다."))

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unexpected error", e)
        return ResponseEntity.internalServerError()
            .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."))
    }
}

open class BusinessException(
    val code: String,
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException(message)

class NotFoundException(resource: String, id: Any) :
    BusinessException("NOT_FOUND", "${resource}을(를) 찾을 수 없습니다. (id=$id)", HttpStatus.NOT_FOUND)

class DuplicateException(field: String) :
    BusinessException("DUPLICATE", "이미 존재하는 ${field}입니다.", HttpStatus.CONFLICT)
