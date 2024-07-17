package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var gifImageView: GifImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userHeroTextView: TextView
    private lateinit var userLvTextView: TextView
    private lateinit var userTitleTextView: TextView
    private lateinit var userCoinTextView: TextView
    private lateinit var progressBarExp: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // image button setting
        val btnQuest = findViewById<ImageView>(R.id.button_quest)
        val btnStore = findViewById<ImageView>(R.id.button_store)
        val btnRanking = findViewById<ImageView>(R.id.button_ranking)

        // user id
        userId = UserHolder.getUser()?.userId.toString()

        // 상단 바를 투명하게 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        btnQuest.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
        }

        btnStore.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        btnRanking.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }

        // UI 요소 초기화
        userNameTextView = findViewById(R.id.user_name)
        userHeroTextView = findViewById(R.id.user_hero)
        userLvTextView = findViewById(R.id.user_lv)
        userTitleTextView = findViewById(R.id.user_title)
        userCoinTextView = findViewById(R.id.text_coin)
        progressBarExp = findViewById(R.id.progressBar_exp)
        gifImageView = findViewById(R.id.gifImageView)

        // Load user data
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        // Update UI with UserHolder data
        UserHolder.getUser()?.let {
            updateUI(it)
        }
    }

    private fun loadUserData() {
        val apiService = ApiClient.retrofit.create(APIServer::class.java)
        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        UserHolder.setUser(it)
                        updateUI(it)
                    }
                } else {
                    Log.e("MainActivity", "Response is not successful")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Failed to get user data", t)
            }
        })
    }

    fun updateUI(user: User) {
        userNameTextView.text = user.userName
        userHeroTextView.text = user.heroName
        userLvTextView.text = "Lv: ${user.level?.div(100)}"
        userTitleTextView.text = user.title
        userCoinTextView.text = user.coin.toString()

        progressBarExp.max = 100
        progressBarExp.progress = user.level?.rem(100) ?: 0

        // character_id에 따라 이미지 설정
        when (user.characterName) {
            "토끼" -> Glide.with(this).load(R.raw.rabbit).into(gifImageView)
            "버섯무리" -> Glide.with(this).load(R.raw.mushroom).into(gifImageView)
            "무사" -> Glide.with(this).load(R.raw.musa).into(gifImageView)
            "아이언맨" -> Glide.with(this).load(R.raw.ironman).into(gifImageView)
            else -> gifImageView.setImageResource(R.raw.default_char)
        }
    }
}