package com.secure.privacyfirst.data

import kotlinx.coroutines.flow.Flow

class PasswordRepository(
    private val passwordDao: PasswordDao,
    private val pinDao: PinDao
) {
    // Password operations
    val allPasswords: Flow<List<PasswordEntity>> = passwordDao.getAllPasswords()
    
    suspend fun insertPassword(password: PasswordEntity): Long {
        return passwordDao.insertPassword(password)
    }
    
    suspend fun updatePassword(password: PasswordEntity) {
        passwordDao.updatePassword(password)
    }
    
    suspend fun deletePassword(password: PasswordEntity) {
        passwordDao.deletePassword(password)
    }
    
    suspend fun getPasswordById(id: Int): PasswordEntity? {
        return passwordDao.getPasswordById(id)
    }
    
    fun searchPasswords(query: String): Flow<List<PasswordEntity>> {
        return passwordDao.searchPasswords(query)
    }
    
    suspend fun deleteAllPasswords() {
        passwordDao.deleteAllPasswords()
    }
    
    // PIN operations
    suspend fun getPin(): PinEntity? {
        return pinDao.getPin()
    }
    
    fun getPinFlow(): Flow<PinEntity?> {
        return pinDao.getPinFlow()
    }
    
    suspend fun savePin(encryptedPin: String) {
        pinDao.insertPin(
            PinEntity(
                encryptedPin = encryptedPin,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
    
    suspend fun deletePin() {
        pinDao.deletePin()
    }
    
    suspend fun verifyPin(inputPin: String): Boolean {
        val storedPin = getPin()
        return storedPin?.encryptedPin == inputPin // Simple comparison for now
    }
}
