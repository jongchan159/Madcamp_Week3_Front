package com.example.project3

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
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

    @PUT("users/")  // 엔드포인트는 실제 API 경로로 변경
    fun updateUsers(@Body users: List<User>): Call<Void>


}

// server가 client한테 보내는 것