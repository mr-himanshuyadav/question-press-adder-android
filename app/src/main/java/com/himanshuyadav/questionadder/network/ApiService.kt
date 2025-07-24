package com.himanshuyadav.questionadder.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// --- Data classes for API responses ---

data class LoginResponse(
    val token: String,
    val user_display_name: String
)

data class Subject(
    val subject_id: Int,
    val subject_name: String
)

data class Topic(
    val topic_id: Int,
    val topic_name: String,
    val subject_id: Int
)

data class Source(
    val source_id: Int,
    val source_name: String,
    val subject_id: Int
)

// --- API endpoint definitions ---

interface ApiService {

    @FormUrlEncoded
    @POST("wp-json/questionpress/v1/token")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("wp-json/questionpress/v1/subjects")
    suspend fun getSubjects(@Header("Authorization") token: String): Response<List<Subject>>

    @GET("wp-json/questionpress/v1/topics")
    suspend fun getTopics(@Header("Authorization") token: String): Response<List<Topic>>

    @GET("wp-json/questionpress/v1/sources")
    suspend fun getSources(@Header("Authorization") token: String): Response<List<Source>>
}