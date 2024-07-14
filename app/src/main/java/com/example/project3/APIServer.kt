package com.example.project3

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface APIServer {

    @GET("api/users/")
    fun getUsers(): Call<List<User>>

    @GET("api/users/{user_id}")
    fun getUserById(@Path("user_id") id: Int): Call<User>

}

// server가 client한테 보내는 것