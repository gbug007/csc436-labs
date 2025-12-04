package com.zybooks.ai4sarapp.data

import android.content.Context
import retrofit2.Response

class SarRepository(
    val context: Context,
    val service: SarService
) {

    // token for accessing API once logged in
    var token: String? = null
        private set

    // user ID for signing out
    var uid: String? = null
        private set

    // sign up and assign values to be used for api
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        org: String,
        role: String
    ): Response<AuthResponse> {
        val request = SignUpRequest(
            name, email, password, org, role
        )
        val response: Response<AuthResponse> = service.signUp(request)
        token = response.body()?.idToken
        uid = response.body()?.user?.uid
        return response
    }

    // login and assign values to be used for api
    suspend fun login(
        email: String,
        password: String
    ): Response<AuthResponse> {
        val request = LogInRequest(email, password)
        val response: Response<AuthResponse> = service.logIn(request)
        token = response.body()?.idToken
        uid = response.body()?.user?.uid
        return response
    }

    // logout and clear values
    suspend fun logout(
    ): Response<AuthResponse> {
        val request = LogOutRequest(uid?:"")
        val response: Response<AuthResponse> = service.logOut(request)
        token = null
        uid = null
        return response
    }

}