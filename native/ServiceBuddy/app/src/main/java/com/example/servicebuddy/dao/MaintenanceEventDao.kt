package com.example.servicebuddy.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.servicebuddy.model.MaintenanceEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceEventDao {
    
    @Query("SELECT * FROM events WHERE isDeletedLocally = 0 ORDER BY dueDate ASC")
    fun getAllEvents(): Flow<List<MaintenanceEvent>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: String): MaintenanceEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: MaintenanceEvent)

    @Update
    suspend fun update(event: MaintenanceEvent)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    
    @Query("SELECT * FROM events WHERE isDirty = 1 OR isDeletedLocally = 1")
    suspend fun getDirtyEvents(): List<MaintenanceEvent>

    
    @Query("DELETE FROM events")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int
}
