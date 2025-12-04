package com.zybooks.ai4sarapp.data


data class LogInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val org: String,
    val role: String
)

data class LogOutRequest(
    val uid: String
)

data class AuthResponse(
    val message: String? = null,   // e.g. "User created successfully", "User signed out successfully"
    val user: AuthUser? = null,    // present on login/signup success
    val idToken: String? = null,   // present on login/signup success
    val error: String? = null      // present on error: "missing-email", "email-exists", etc.
) {
    data class AuthUser(
        val uid: String,
        val email: String?,
        val displayName: String? = null,
        val photoURL: String? = null
        // add more fields later if you need them
    )
}



