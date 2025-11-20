package com.secure.privacyfirst.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("api/whitelist/")
    suspend fun getWhitelist(@Header("Authorization") token: String): Response<WhitelistResponse>
    
    @POST("api/whitelist/add")
    suspend fun addUrl(
        @Header("Authorization") token: String,
        @Body request: AddUrlRequest
    ): Response<SuccessResponse>
    
    @PUT("api/whitelist/update")
    suspend fun updateUrl(
        @Header("Authorization") token: String,
        @Body request: UpdateUrlRequest
    ): Response<SuccessResponse>
    
    @HTTP(method = "DELETE", path = "api/whitelist/delete", hasBody = true)
    suspend fun deleteUrl(
        @Header("Authorization") token: String,
        @Body request: DeleteUrlRequest
    ): Response<SuccessResponse>
}
