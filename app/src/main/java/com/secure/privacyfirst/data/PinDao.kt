package com.secure.privacyfirst.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PinDao {
    @Query("SELECT * FROM pin WHERE id = 1")
    suspend fun getPin(): PinEntity?
    
    @Query("SELECT * FROM pin WHERE id = 1")
    fun getPinFlow(): Flow<PinEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: PinEntity)
    
    @Query("DELETE FROM pin")
    suspend fun deletePin()
}
