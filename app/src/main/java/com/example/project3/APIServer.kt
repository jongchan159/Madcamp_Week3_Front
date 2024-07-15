package com.example.project3

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIServer {

    @GET("users/")
    fun getUsers(): Call<List<User>>

    @GET("users/{user_id}")
    fun getUserById(@Path("user_id") id: Int): Call<User>

    @GET("diaries/")
    fun getDiaries(): Call<List<Diary>>

    @GET("quests/")
    fun getQuests(): Call<List<Quest>>

    @GET("storeItems")
    fun getStoreItems(): Call<List<String>>

    @GET("inventoryItems")
    fun getInventoryItems(): Call<List<String>>


}

// server가 client한테 보내는 것