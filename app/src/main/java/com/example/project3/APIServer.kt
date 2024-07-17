package com.example.project3

import com.example.project3.Class.Item
import com.example.project3.Class.Receipt
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
    @POST("user_login/")
    fun sendUserId(@Body idRequest: IdRequest): Call<Void>

    @GET("checkUser")
    fun checkUser(@Query("id") id: String): Call<GoogleloginActivity.UserResponse>

    @GET("users/")
    fun getUsers(): Call<List<User>>

    @GET("users/{user_id}")
    fun getUserById(@Path("user_id") id: String): Call<User>

    @GET("diaries/")
    fun getDiaries(): Call<List<Diary>>

    @GET("diaries/{user_id}")
    fun getDiaryByUserId(@Query("user_id") id: String): Call<Diary>

    @GET("quests/")
    fun getQuests(): Call<List<Quest>>

    @GET("quests/{user_id}")
    fun getQuestByUserId(@Query("user_id") id: String): Call<Quest>

    @GET("quests/{questId}/")
    fun getQuestProgress(@Path("questId") questId: Int): Call<Quest>

    @POST("generate_quests/")
    fun updateAllUsersQuests(): Call<Void>

    @POST("generate_quests/{userId}/")
    fun updateUserQuests(@Path("userId") userId: String): Call<Void>

    @POST("users/")
    fun createUser(@Body user: User): Call<User>

    @POST("diaries/")
    fun createDiary(@Body diary: Diary): Call<Diary>

    @PUT("users/update/")  // 엔드포인트는 실제 API 경로로 변경
    fun updateUsers(@Body users: List<User>): Call<Void>

    @PUT("quests/{questId}/")
    fun updateQuestProgress(@Path("questId") questId: Int, @Body quest: Quest): Call<Void>

    // 아이템 관련
    @GET("items/")
    fun getItems(): Call<List<Item>>

    // 아이템 구매
    @POST("receipts/")
    fun createReceipts(@Body receipt: Receipt): Call<Receipt>

    // 유저 id가 같은 모든 아이템 조회
    @GET("receipts/{userId}/")
    fun getUserReceipts(@Path("userId") userId: String): Call<List<Receipt>>

    // 모든 구매내역 조회
    @GET("receipts/")
    fun getAllReceipts(): Call<List<Receipt>>

    //경험치 업뎃
    @PUT("users/{userId}/")
    fun updateUser(@Path("userId") userId: String, @Body user: User): Call<User>
}

// server가 client한테 보내는 것