package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.gson.Gson
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // image button setting
        val btnQuest = findViewById<ImageView>(R.id.button_quest)
        val btnStore = findViewById<ImageView>(R.id.button_store)
        val btnRanking = findViewById<ImageView>(R.id.button_ranking)

        // 상단 바를 투명하게 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        val gifImageView: GifImageView = findViewById(R.id.gifImageView)

        // 로컬 리소스에서 GIF 로드
        val gifDrawable = GifDrawable(resources, (R.raw.default_char))
        gifImageView.setImageDrawable(gifDrawable)

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

        // profile text setting
/*        // 테스트용 유저
        val json = """
        {
            "user_name": "송종찬",
            "hero_name": "토니 스타크",
            "exp": 7550,
            "title": "현경",
            "coin": 1500,
            "age": 25,
            "background_id": 1,
            "character_id": 1001
        }
        """
        // JSON 데이터 파싱
        val user = parseUserJson(json)*/

        // UI 요소 초기화
        val userNameTextView: TextView = findViewById(R.id.user_name)
        val userHeroTextView: TextView = findViewById(R.id.user_hero)
        val userLvTextView: TextView = findViewById(R.id.user_lv)
        val userTitleTextView: TextView = findViewById(R.id.user_title)
        val userCoinTextView: TextView = findViewById(R.id.text_coin)
        val progressBarExp: ProgressBar = findViewById(R.id.progressBar_exp)

        val userId = "1001010110111"

        // API을 사용하여 서버에서 데이터 가져오기
        val apiService = ApiClient.retrofit.create(APIServer::class.java)
        apiService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()

                    user?.let {
                        // UI에 데이터 설정
                        userNameTextView.text = "Name: ${it.userName}"
                        userHeroTextView.text = "Hero Name: ${it.heroName}"
                        userLvTextView.text = "Lv: ${it.level?.div(100)}"
                        userTitleTextView.text = "${it.title}"
                        userCoinTextView.text = "${it.coin}"

                        progressBarExp.max = 100
                        it.level?.let { level ->
                            progressBarExp.progress = level % 100
                        }
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

    private fun parseUserJson(json: String): User {
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }
}
