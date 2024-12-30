package com.example.project3

import com.google.gson.annotations.SerializedName

data class Diary(

    @SerializedName("contents")
    var contents: String,

    @SerializedName("user")
    var userId: String, // User id 받아오는 것 같다.
)