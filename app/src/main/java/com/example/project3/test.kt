package com.example.project3

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val gifImageView: GifImageView = findViewById(R.id.gifImageView)

        // 로컬 리소스에서 GIF 로드
        val gifDrawable = GifDrawable(resources, R.raw.rabbit)
        gifImageView.setImageDrawable(gifDrawable)
    }
}