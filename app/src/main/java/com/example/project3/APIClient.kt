package com.example.project3

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val SERVER_IP = "172.10.7.121"
object ApiClient {
    private const val BASE_URL = "http://$SERVER_IP:80/api/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(500, TimeUnit.SECONDS)
        .readTimeout(500, TimeUnit.SECONDS)
        .writeTimeout(500, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: APIServer by lazy {
        retrofit.create(APIServer::class.java)
    }
}