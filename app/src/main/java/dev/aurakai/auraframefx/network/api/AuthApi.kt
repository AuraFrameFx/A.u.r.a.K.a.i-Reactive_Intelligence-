package dev.aurakai.auraframefx.network.api

import dev.aurakai.auraframefx.network.RefreshTokenRequest
import dev.aurakai.auraframefx.network.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API interface for authentication-related operations.
 */
interface AuthApi {

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param request The refresh token request.
     * @return A response containing the new access and refresh tokens.
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest,
    ): Response<TokenResponse>

    /**
     * Logs in a user with the provided credentials.
     *
     * @param request The login request containing user credentials.
     * @return A response containing the access and refresh tokens.
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<TokenResponse>

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details.
     * @return A response indicating the result of the registration.
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<Unit>

    /**
     * Logs out the current user.
     *
     * @return A response indicating the result of the logout operation.
     */
    @POST("auth/logout")
    suspend fun logout(): Response<Unit>
}

/**
 * Data class for login request.
 */
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false,
)

/**
 * Data class for registration request.
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
)
