package com.example.servicebuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.servicebuddy.dao.MaintenanceEventDao
import com.example.servicebuddy.model.MaintenanceEvent

import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.UUID
import java.util.Calendar

@Database(entities = [MaintenanceEvent::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): MaintenanceEventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        private val applicationScope = CoroutineScope(Dispatchers.IO)

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "service_buddy_database"
                )
                    .addCallback(AppDatabaseCallback(applicationScope))
                    .build().also { INSTANCE = it }
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val eventDao = database.eventDao()
                    val calendar = Calendar.getInstance()

                    calendar.set(2025, Calendar.JANUARY, 15)
                    val event1 = MaintenanceEvent(
                        id = "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                        vehicleIdentifier = "VW GOLF 7",
                        title = "Oil Change",
                        description = "Synthetic oil and filter replacement.",
                        category = "SERVICE",
                        dueDate = calendar.time,
                        status = "PENDING",
                        price = 75.00
                    )
                    eventDao.insert(event1)

                    calendar.set(2025, Calendar.MARCH, 1)
                    val event2 = MaintenanceEvent(
                        id = "b2c3d4e5-f6a7-8901-2345-67890abcdef1",
                        vehicleIdentifier = "VW GOLF 7",
                        title = "Insurance Renewal",
                        description = "Annual car insurance policy renewal.",
                        category = "DOCUMENT",
                        dueDate = calendar.time,
                        status = "COMPLETED",
                        price = 1200.00
                    )
                    eventDao.insert(event2)

                    calendar.set(2025, Calendar.APRIL, 20)
                    val event3 = MaintenanceEvent(
                        id = "c3d4e5f6-a7b8-9012-3456-7890abcdef12",
                        vehicleIdentifier = "AUDI A3",
                        title = "Tire Rotation",
                        description = "Rotate tires and check pressure.",
                        category = "SERVICE",
                        dueDate = calendar.time,
                        status = "OVERDUE",
                        price = 30.00
                    )
                    eventDao.insert(event3)

                    calendar.set(2025, Calendar.MAY, 5)
                    val event4 = MaintenanceEvent(
                        id = "d4e5f6a7-b8c9-0123-4567-890abcdef123",
                        vehicleIdentifier = "AUDI A3",
                        title = "Brake Pad Replacement",
                        description = "Replace front and rear brake pads.",
                        category = "SERVICE",
                        dueDate = calendar.time,
                        status = "PENDING",
                        price = 250.00
                    )
                    eventDao.insert(event4)

                    calendar.set(2025, Calendar.JUNE, 10)
                    val event5 = MaintenanceEvent(
                        id = "e5f6a7b8-c9d0-1234-5678-90abcdef1234",
                        vehicleIdentifier = "BMW 3 Series",
                        title = "Annual Inspection",
                        description = "State-mandated annual vehicle inspection.",
                        category = "DOCUMENT",
                        dueDate = calendar.time,
                        status = "PENDING",
                        price = 100.00
                    )
                    eventDao.insert(event5)
                }
            }
        }
    }
}
