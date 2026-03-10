package com.example.servicebuddy.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "events")
data class MaintenanceEvent(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var vehicleIdentifier: String,
    var title: String,
    var description: String?,
    var category: String,
    var dueDate: Date,
    var status: String,
    var price: Double,

    var isDirty: Boolean = false,         
    var isDeletedLocally: Boolean = false 
)
