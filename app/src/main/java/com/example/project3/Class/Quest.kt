package com.example.project3

import com.google.gson.annotations.SerializedName

data class Quest(
    @SerializedName("quest_id")
    var questId: Int,

    @SerializedName("contents")
    var contents: String,

    @SerializedName("is_complete")
    var isComplete: Boolean,

    @SerializedName("progress_time")
    var progressTime: String?,

    @SerializedName("complete_time")
    var completeTime: String?,

    @SerializedName("user")
    var userId: String?,

    @SerializedName("type")
    var type: String,

    @SerializedName("created_at")
    var createdAt: String,
)