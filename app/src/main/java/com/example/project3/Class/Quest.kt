package com.example.project3

import com.google.gson.annotations.SerializedName

data class Quest(
    @SerializedName("quest_id")
    val questId: Int,

    @SerializedName("contents")
    val contents: String,

    @SerializedName("is_complete")
    val isComplete: Boolean,

    @SerializedName("progress_time")
    val progressTime: String?,

    @SerializedName("complete_time")
    val completeTime: String?,

    @SerializedName("user")
    val userId: Int?,

    @SerializedName("type")
    val type: String,

    @SerializedName("created_at")
    val createdAt: String,
)