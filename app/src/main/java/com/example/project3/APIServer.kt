package com.example.project3

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class IdRequest(val idToken: String)

interface APIServer {

    // 구글 로그인 토큰 주기
    @Headers("Content-Type: application/json")
    @POST("user_id/")
    fun sendUserId(@Body idRequest: IdRequest): Call<Void>

    @GET("users/")
    fun getUsers(): Call<List<User>>

    @GET("users/{user_id}")
    fun getUserById(@Path("user_id") id: Int): Call<User>

    @GET("diaries/")
    fun getDiaries(): Call<List<Diary>>

    @GET("diaries/{user_id}")
    fun getDiaryByUserId(@Query("user_id") id: Int): Call<Diary>

    @GET("quests/")
    fun getQuests(): Call<List<Quest>>

    @GET("quests/{user_id}")
    fun getQuestByUserId(@Query("user_id") id: Int): Call<Quest>

    @GET("quests/{questId}/")
    fun getQuestProgress(@Path("questId") questId: Int): Call<Quest>

    @POST("generate_quests/")
    fun updateAllUsersQuests(): Call<Void>

    @PUT("users/")  // 엔드포인트는 실제 API 경로로 변경
    fun updateUsers(@Body users: List<User>): Call<Void>

    @PUT("quests/{questId}/")
    fun updateQuestProgress(@Path("questId") questId: Int, @Body quest: Quest): Call<Void>
}

// server가 client한테 보내는 것