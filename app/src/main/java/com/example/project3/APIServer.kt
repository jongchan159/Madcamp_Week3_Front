package com.example.project3

import retrofit2.Call
import retrofit2.http.GET

interface APIServer {

    @GET("users/")
    fun getUsers(): Call<List<User>>

    @GET("users/")  // 엔드포인트는 실제 API 경로로 변경
    fun getUser(): Call<User>

    @GET("diaries/")
    fun getDiaries(): Call<List<Diary>>

    @GET("quests/")
    fun getQuests(): Call<List<Quest>>
}

// server가 client한테 보내는 것