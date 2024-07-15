package com.example.project3

import com.google.gson.annotations.SerializedName

data class Diary(
    @SerializedName("diary_id")
    var diaryId: Int,

    @SerializedName("contents")
    var contents: String,

    @SerializedName("user")
    var userId: Int, // User id 받아오는 것 같다.

    @SerializedName("created_at")
    var createdAt: String,
)