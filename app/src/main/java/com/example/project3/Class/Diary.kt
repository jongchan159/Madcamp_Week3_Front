package com.example.project3

import com.google.gson.annotations.SerializedName

data class Diary(
    @SerializedName("diary_id")
    val diaryId: Int,

    @SerializedName("contents")
    val contents: String,

    @SerializedName("user")
    val userId: Int, // User id 받아오는 것 같다.

    @SerializedName("created_at")
    val createdAt: String,
)