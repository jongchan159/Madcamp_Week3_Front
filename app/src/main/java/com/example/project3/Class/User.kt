package com.example.project3

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    var userId: String?,

    @SerializedName("user_name")
    var userName: String?,

    @SerializedName("hero_name")
    var heroName: String?,

    @SerializedName("level")
    var level: Int?,

    @SerializedName("title")
    var title: String?,

    @SerializedName("coin")
    var coin: Int?,

    @SerializedName("age")
    var age: Int?,

    @SerializedName("ranking")
    var ranking: Int?,

    @SerializedName("background_id")
    var backgroundId: Int?,

    @SerializedName("character_id")
    var characterId: Int?,
)
