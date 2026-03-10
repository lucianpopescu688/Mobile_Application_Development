package com.example.servicebuddy.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"
    private const val WS_URL = "ws://10.0.2.2:3000/ws"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: MaintenanceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MaintenanceApi::class.java)
    }

    fun createWebSocket(listener: WebSocketListener): WebSocket {
        val request = Request.Builder().url(WS_URL).build()
        return okHttpClient.newWebSocket(request, listener)
    }
}