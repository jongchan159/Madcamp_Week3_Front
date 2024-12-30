package com.example.project3

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    var userId: String? = "",

    @SerializedName("user_name")
    var userName: String? = "",

    @SerializedName("hero_name")
    var heroName: String? = "",

    @SerializedName("hero_alias")
    var heroAlias: String? = "",

    @SerializedName("exp")
    var exp: Int? = 100,

    @SerializedName("title")
    var title: String? = "",

    @SerializedName("coin")
    var coin: Int? = 0,

    @SerializedName("ranking")
    var ranking: Int? = 0,

    @SerializedName("background_id")
    var backgroundId: Int? = null,

    @SerializedName("character_id")
    var characterName: String = "default_char",
)

data class UserResponse(
    val exists: Boolean
)
