package com.secure.privacyfirst.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pin")
data class PinEntity(
    @PrimaryKey
    val id: Int = 1, // Only one PIN stored
    val encryptedPin: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
