package com.himanshuyadav.questionadder.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Data class to represent the JSON we send for login
data class LoginRequest(
    val username: String,
    val password: String
)

// Data class to represent the JSON response we get on successful login
data class LoginResponse(
    val token: String,
    val user_display_name: String
)

// This interface defines the API endpoints
interface ApiService {

    @FormUrlEncoded
    @POST("wp-json/questionpress/v1/token")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

}