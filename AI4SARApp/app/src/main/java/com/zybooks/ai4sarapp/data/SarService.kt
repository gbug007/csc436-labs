package com.zybooks.ai4sarapp.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface SarService {

    // authentication methods
    @POST("auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun logIn(
        @Body request: LogInRequest
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logOut(
        @Body request: LogOutRequest
    ): Response<AuthResponse>

    // get incidents
    @GET("incidents")
    suspend fun getIncidents(
        @Header("Authorization") auth: String
    ): List<IncidentDocument>

}