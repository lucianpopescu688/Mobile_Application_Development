package com.example.servicebuddy.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.servicebuddy.dao.MaintenanceEventDao
import com.example.servicebuddy.model.MaintenanceEvent
import com.example.servicebuddy.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class EventsRepository(private val eventDao: MaintenanceEventDao) {

    val allEvents: Flow<List<MaintenanceEvent>> = eventDao.getAllEvents()

    private val _connectionStatus = MutableLiveData(true)
    val connectionStatus: LiveData<Boolean> = _connectionStatus

    private var webSocket: WebSocket? = null

    init {
        initWebSocket()
    }

    private fun initWebSocket() {
        Log.d("Repo", "Initializing WebSocket...")
        
        try { webSocket?.close(1000, "Reconnecting") } catch (e: Exception) {}

        webSocket = RetrofitClient.createWebSocket(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("Repo", "WebSocket Opened")
                _connectionStatus.postValue(true)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("Repo", "WebSocket Message: $text")
                CoroutineScope(Dispatchers.IO).launch { fetchFromNetwork() }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionStatus.postValue(false)
            }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _connectionStatus.postValue(false)
            }
        })
    }

    
    suspend fun refreshEvents() {
        
        if (_connectionStatus.value != true) {
            Log.d("Repo", "Manual Refresh: Attempting WebSocket Reconnect...")
            initWebSocket()
        }

        syncLocalChanges()
        fetchFromNetwork()
    }

    private suspend fun fetchFromNetwork() {
        try {
            val serverEvents = RetrofitClient.api.getEvents()
            serverEvents.forEach { event ->
                val local = eventDao.getEventById(event.id)
                if (local == null || !local.isDirty) {
                    eventDao.insert(event)
                }
            }
        } catch (e: Exception) {
            Log.e("Repo", "Fetch error: ${e.message}")
        }
    }

    suspend fun createEvent(event: MaintenanceEvent): Boolean {
        event.isDirty = true
        eventDao.insert(event)
        try {
            val response = RetrofitClient.api.addEvent(event)
            if (response.isSuccessful) {
                event.isDirty = false
                eventDao.insert(event)
            }
        } catch (e: Exception) {
            Log.e("Repo", "Create offline: ${e.message}")
        }
        return true
    }

    suspend fun updateEvent(event: MaintenanceEvent): Boolean {
        event.isDirty = true
        eventDao.insert(event)
        try {
            val response = RetrofitClient.api.updateEvent(event.id, event)
            if (response.isSuccessful) {
                event.isDirty = false
                eventDao.insert(event)
            }
        } catch (e: Exception) {
            Log.e("Repo", "Update offline: ${e.message}")
        }
        return true
    }

    suspend fun getEvent(id: String): MaintenanceEvent? = eventDao.getEventById(id)

    suspend fun deleteEvent(id: String): Boolean {
        val event = eventDao.getEventById(id) ?: return false
        event.isDeletedLocally = true
        eventDao.update(event)
        try {
            val response = RetrofitClient.api.deleteEvent(id)
            if (response.isSuccessful) {
                eventDao.deleteById(id)
            }
        } catch (e: Exception) {
            Log.e("Repo", "Delete offline: ${e.message}")
        }
        return true
    }

    private suspend fun syncLocalChanges() {
        val dirtyEvents = eventDao.getDirtyEvents()
        dirtyEvents.forEach { event ->
            if (event.isDeletedLocally) {
                deleteEvent(event.id)
            } else if (event.isDirty) {
                try {
                    val response = RetrofitClient.api.updateEvent(event.id, event)
                    if (response.isSuccessful) {
                        event.isDirty = false
                        eventDao.insert(event)
                    } else if (response.code() == 404) {
                        val postResponse = RetrofitClient.api.addEvent(event)
                        if (postResponse.isSuccessful) {
                            event.isDirty = false
                            eventDao.insert(event)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Repo", "Sync failed for ${event.id}")
                }
            }
        }
    }
}
