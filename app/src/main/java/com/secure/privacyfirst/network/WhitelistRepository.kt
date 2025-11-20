package com.secure.privacyfirst.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WhitelistRepository(private val context: Context) {
    
    private val apiService = RetrofitClient.apiService
    private val tokenManager = TokenManager(context)
    
    companion object {
        private const val TAG = "WhitelistRepository"
        private const val ADMIN_USERNAME = "admin"
        private const val ADMIN_PASSWORD = "pass123"
    }
    
    /**
     * Login and store JWT token
     */
    suspend fun login(username: String = ADMIN_USERNAME, password: String = ADMIN_PASSWORD): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    tokenManager.saveToken(loginResponse.token, loginResponse.expiresIn)
                    Log.d(TAG, "Login successful, token saved")
                    Result.success(loginResponse.token)
                } else {
                    val error = "Login failed: ${response.code()}"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login error: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get whitelist URLs from server
     */
    suspend fun getWhitelist(): Result<List<String>> {
        return withContext(Dispatchers.IO) {
            try {
                // Check if token is valid, if not login first
                if (!tokenManager.isTokenValid()) {
                    Log.d(TAG, "Token expired or missing, logging in...")
                    val loginResult = login()
                    if (loginResult.isFailure) {
                        return@withContext Result.failure(loginResult.exceptionOrNull() ?: Exception("Login failed"))
                    }
                }
                
                val authHeader = tokenManager.getAuthHeader()
                if (authHeader == null) {
                    Log.e(TAG, "No auth token available")
                    return@withContext Result.failure(Exception("No authentication token"))
                }
                
                val response = apiService.getWhitelist(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    val urls = response.body()!!.urls
                    Log.d(TAG, "Whitelist fetched successfully: ${urls.size} URLs")
                    Result.success(urls)
                } else {
                    val error = "Failed to fetch whitelist: ${response.code()}"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching whitelist: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Add URL to whitelist
     */
    suspend fun addUrl(url: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!tokenManager.isTokenValid()) {
                    login()
                }
                
                val authHeader = tokenManager.getAuthHeader() ?: return@withContext Result.failure(
                    Exception("No authentication token")
                )
                
                val response = apiService.addUrl(authHeader, AddUrlRequest(url))
                if (response.isSuccessful) {
                    Log.d(TAG, "URL added successfully: $url")
                    Result.success(response.body()?.message ?: "URL added")
                } else {
                    Result.failure(Exception("Failed to add URL: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding URL: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Update URL in whitelist
     */
    suspend fun updateUrl(oldUrl: String, newUrl: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!tokenManager.isTokenValid()) {
                    login()
                }
                
                val authHeader = tokenManager.getAuthHeader() ?: return@withContext Result.failure(
                    Exception("No authentication token")
                )
                
                val response = apiService.updateUrl(authHeader, UpdateUrlRequest(oldUrl, newUrl))
                if (response.isSuccessful) {
                    Log.d(TAG, "URL updated successfully")
                    Result.success(response.body()?.message ?: "URL updated")
                } else {
                    Result.failure(Exception("Failed to update URL: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating URL: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Delete URL from whitelist
     */
    suspend fun deleteUrl(url: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                if (!tokenManager.isTokenValid()) {
                    login()
                }
                
                val authHeader = tokenManager.getAuthHeader() ?: return@withContext Result.failure(
                    Exception("No authentication token")
                )
                
                val response = apiService.deleteUrl(authHeader, DeleteUrlRequest(url))
                if (response.isSuccessful) {
                    Log.d(TAG, "URL deleted successfully: $url")
                    Result.success(response.body()?.message ?: "URL deleted")
                } else {
                    Result.failure(Exception("Failed to delete URL: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting URL: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
    
    /**
     * Logout and clear token
     */
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenManager.clearToken()
            Log.d(TAG, "Logged out, token cleared")
        }
    }
}
