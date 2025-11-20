package com.secure.privacyfirst.network

import com.google.gson.annotations.SerializedName

// Request models
data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class AddUrlRequest(
    @SerializedName("url")
    val url: String
)

data class UpdateUrlRequest(
    @SerializedName("oldUrl")
    val oldUrl: String,
    @SerializedName("newUrl")
    val newUrl: String
)

data class DeleteUrlRequest(
    @SerializedName("url")
    val url: String
)

// Response models
data class LoginResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("expiresIn")
    val expiresIn: String
)

data class WhitelistResponse(
    @SerializedName("urls")
    val urls: List<String>,
    @SerializedName("count")
    val count: Int
)

data class ApiErrorResponse(
    @SerializedName("message")
    val message: String
)

data class SuccessResponse(
    @SerializedName("message")
    val message: String
)
