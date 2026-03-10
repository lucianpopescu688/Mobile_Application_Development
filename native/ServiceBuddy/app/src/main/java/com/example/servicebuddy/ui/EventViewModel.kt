package com.example.servicebuddy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.servicebuddy.database.AppDatabase
import com.example.servicebuddy.model.MaintenanceEvent
import com.example.servicebuddy.repository.EventsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import androidx.lifecycle.asLiveData

class EventViewModel(application: Application) : AndroidViewModel(application) {

    val repository = EventsRepository(AppDatabase.getDatabase(application).eventDao())
    val isOnline = repository.connectionStatus

    private val _snackbarMessage = MutableLiveData<String?>()
    val snackbarMessage: LiveData<String?> = _snackbarMessage

    private val _searchQuery = MutableStateFlow("")

    val events: LiveData<List<MaintenanceEvent>> = _searchQuery.combine(repository.allEvents) { query, events ->
        val filteredEvents = if (query.isBlank()) {
            events
        } else {
            events.filter { event ->
                event.title.contains(query, ignoreCase = true) ||
                        event.vehicleIdentifier.contains(query, ignoreCase = true)
            }
        }
        filteredEvents.sortedWith(eventComparator)
    }.asLiveData()

    private val eventComparator = Comparator<MaintenanceEvent> { event1, event2 ->
        val statusOrder1 = getStatusOrder(event1)
        val statusOrder2 = getStatusOrder(event2)
        if (statusOrder1 != statusOrder2) {
            statusOrder1.compareTo(statusOrder2)
        } else {
            event1.dueDate.compareTo(event2.dueDate)
        }
    }

    private fun getStatusOrder(event: MaintenanceEvent): Int {
        val now = System.currentTimeMillis()
        val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000
        val dueDateMillis = event.dueDate.time

        return when (event.status) {
            "PENDING" -> 1
            "OVERDUE" -> 2
            "COMPLETED" -> 5
            else -> when {
                dueDateMillis < now -> 2
                dueDateMillis < now + sevenDaysInMillis -> 3
                else -> 4
            }
        }
    }

    init {
        refreshData()
    }

    fun onSnackbarMessageShown() {
        _snackbarMessage.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshData() = viewModelScope.launch(Dispatchers.IO) {
        repository.refreshEvents()
    }

    fun addEvent(event: MaintenanceEvent) = viewModelScope.launch(Dispatchers.IO) {
        if (repository.createEvent(event)) {
            _snackbarMessage.postValue("Event '${event.title}' for '${event.vehicleIdentifier}' Added")
        } else {
            _snackbarMessage.postValue("Failed to add event '${event.title}' for '${event.vehicleIdentifier}'")
        }
    }

    fun updateEventById(oldId: String, event: MaintenanceEvent) = viewModelScope.launch(Dispatchers.IO) {
        if (oldId != event.id) {
            val deleteSuccess = repository.deleteEvent(oldId)
            val createSuccess = repository.createEvent(event)
            if (deleteSuccess && createSuccess) {
                _snackbarMessage.postValue("Event '${event.title}' for '${event.vehicleIdentifier}' Replaced")
            } else {
                _snackbarMessage.postValue("Failed to replace event '${event.title}' for '${event.vehicleIdentifier}'")
            }
        } else {
            if (repository.updateEvent(event)) {
                _snackbarMessage.postValue("Event '${event.title}' for '${event.vehicleIdentifier}' Updated")
            } else {
                _snackbarMessage.postValue("Failed to update event '${event.title}' for '${event.vehicleIdentifier}'")
            }
        }
    }

    fun deleteEvent(id: String) = viewModelScope.launch(Dispatchers.IO) {
        val eventToDelete = repository.getEvent(id)
        if (eventToDelete == null) {
            _snackbarMessage.postValue("Failed to delete event: Event not found")
            return@launch
        }
        if (repository.deleteEvent(id)) {
            _snackbarMessage.postValue("Event '${eventToDelete.title}' for '${eventToDelete.vehicleIdentifier}' Deleted")
        } else {
            _snackbarMessage.postValue("Failed to delete event '${eventToDelete.title}' for '${eventToDelete.vehicleIdentifier}'")
        }
    }

    suspend fun getEventByIdSuspend(id: String) = repository.getEvent(id)

    fun getEventById(id: String, onResult: (MaintenanceEvent?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val event = repository.getEvent(id)
            withContext(Dispatchers.Main) {
                onResult(event)
            }
        }
    }
}
