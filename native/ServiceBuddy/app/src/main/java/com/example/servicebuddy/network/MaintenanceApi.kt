package com.example.servicebuddy.network

import com.example.servicebuddy.model.MaintenanceEvent
import retrofit2.Response
import retrofit2.http.*

interface MaintenanceApi {
    @GET("/events")
    suspend fun getEvents(): List<MaintenanceEvent>

    @POST("/event")
    suspend fun addEvent(@Body event: MaintenanceEvent): Response<MaintenanceEvent>

    @DELETE("/event/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Unit>

    @PUT("/event/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: MaintenanceEvent): Response<MaintenanceEvent>
}
