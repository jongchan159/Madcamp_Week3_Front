package com.example.project3

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_name")
    val userName: String?,

    @SerializedName("hero_name")
    val heroName: String?,

    @SerializedName("level")
    val level: Int?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("coin")
    val coin: Int?,

    @SerializedName("age")
    val age: Int?,

    @SerializedName("background_id")
    val backgroundId: Int?,

    @SerializedName("character_id")
    val characterId: Int?,
)
