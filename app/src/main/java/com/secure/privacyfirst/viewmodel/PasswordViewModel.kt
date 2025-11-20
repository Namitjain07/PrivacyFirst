package com.secure.privacyfirst.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.secure.privacyfirst.data.AppDatabase
import com.secure.privacyfirst.data.CryptoUtils
import com.secure.privacyfirst.data.PasswordEntity
import com.secure.privacyfirst.data.PasswordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PasswordRepository
    private val secretKey = CryptoUtils.getOrCreateKey()
    
    val allPasswords: StateFlow<List<PasswordEntity>>
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isPinSet = MutableStateFlow(false)
    val isPinSet: StateFlow<Boolean> = _isPinSet.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = PasswordRepository(database.passwordDao(), database.pinDao())
        
        allPasswords = repository.allPasswords
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
        
        viewModelScope.launch {
            repository.getPinFlow().collect { pin ->
                _isPinSet.value = pin != null
            }
        }
    }
    
    fun addPassword(
        title: String,
        username: String,
        password: String,
        website: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val encryptedPassword = CryptoUtils.encrypt(password, secretKey)
                val passwordEntity = PasswordEntity(
                    title = title,
                    username = username,
                    encryptedPassword = encryptedPassword,
                    website = website,
                    notes = notes
                )
                repository.insertPassword(passwordEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun updatePassword(
        id: Int,
        title: String,
        username: String,
        password: String,
        website: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val encryptedPassword = CryptoUtils.encrypt(password, secretKey)
                val passwordEntity = PasswordEntity(
                    id = id,
                    title = title,
                    username = username,
                    encryptedPassword = encryptedPassword,
                    website = website,
                    notes = notes,
                    createdAt = 0, // Will be preserved from original
                    updatedAt = System.currentTimeMillis()
                )
                repository.updatePassword(passwordEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deletePassword(password: PasswordEntity) {
        viewModelScope.launch {
            repository.deletePassword(password)
        }
    }
    
    fun getDecryptedPassword(encryptedPassword: String): String {
        return try {
            CryptoUtils.decrypt(encryptedPassword, secretKey)
        } catch (e: Exception) {
            "Error decrypting"
        }
    }
    
    suspend fun getPasswordById(id: Int): PasswordEntity? {
        return repository.getPasswordById(id)
    }
    
    fun searchPasswords(query: String) {
        _searchQuery.value = query
    }
    
    // PIN operations
    fun savePin(pin: String) {
        viewModelScope.launch {
            val hashedPin = CryptoUtils.hashPin(pin)
            repository.savePin(hashedPin)
        }
    }
    
    suspend fun verifyPin(pin: String): Boolean {
        val hashedPin = CryptoUtils.hashPin(pin)
        val storedPin = repository.getPin()
        return storedPin?.encryptedPin == hashedPin
    }
    
    fun deleteAllPasswords() {
        viewModelScope.launch {
            repository.deleteAllPasswords()
        }
    }
}
