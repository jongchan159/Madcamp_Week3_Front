package com.example.project3

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
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
    private lateinit var userRankTextView: TextView
    private lateinit var progressBarExp: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // image button setting
        val btnQuest = findViewById<ImageView>(R.id.button_quest)
        val btnStore = findViewById<ImageView>(R.id.button_store)
        val btnRanking = findViewById<ImageView>(R.id.button_ranking)
        val imagePaper = findViewById<ImageView>(R.id.image_paper)

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

        imagePaper.setOnClickListener {
            showRankingDialog(UserHolder.getUser())
        }

        // UI 요소 초기화
        userNameTextView = findViewById(R.id.user_name)
        userHeroTextView = findViewById(R.id.user_hero)
        userLvTextView = findViewById(R.id.user_lv)
        userTitleTextView = findViewById(R.id.user_title)
        userCoinTextView = findViewById(R.id.text_coin)
        userRankTextView = findViewById(R.id.user_ranking)
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
            checkLevelUp(it)
        }
    }

    override fun onPause() {
        super.onPause()
        // Save current user level to SharedPreferences
        UserHolder.getUser()?.let {
            saveUserLevelToPrefs(it.exp?.div(100) ?: 0)
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
                        checkLevelUp(it)
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

    private fun checkLevelUp(user: User) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val savedLevel = prefs.getInt("user_level", 0)
        val currentLevel = user.exp?.div(100) ?: 0
        if (currentLevel > savedLevel) {
            // Add 100 coins for leveling up
            user.coin = (user.coin ?: 0) + 100
            val newTitle = getTitleForLevel(currentLevel)
            if (newTitle != user.title) {
                user.title = newTitle
                showTitleUpDialog(newTitle)
            }
            UserHolder.setUser(user)
            updateUI(user)
            updateCurrentUserOnServer(user)
            showLevelUpDialog(currentLevel)
            saveUserLevelToPrefs(currentLevel)
        }
    }

    private fun saveUserLevelToPrefs(level: Int) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("user_level", level)
        editor.apply()
    }

    private fun showLevelUpDialog(newLevel: Int) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_level, null)
        dialog.setContentView(dialogView)

        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogMessage: TextView = dialogView.findViewById(R.id.dialog_message)
        val dialogOkButton: Button = dialogView.findViewById(R.id.dialog_ok_button)

        dialogTitle.text = "레벨 업"
        dialogMessage.text = "축하드립니다! 당신의 영웅은 $newLevel 레벨에 도달했습니다. 당신은 100금화를 획득합니다!"

        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showTitleUpDialog(newTitle: String) {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)
        dialog.setContentView(dialogView)

        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogMessage: TextView = dialogView.findViewById(R.id.dialog_message)
        val dialogOkButton: Button = dialogView.findViewById(R.id.dialog_ok_button)

        dialogTitle.text = "경지가 올랐습니다!"
        dialogMessage.text = "축하드립니다! 당신의 영웅은 ${newTitle}의 경지에 도달했습니다!"

        dialogOkButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getTitleForLevel(level: Int): String {
        return when {
            level >= 400 -> "초월자"
            level >= 350 -> "생사경"
            level >= 300 -> "현경"
            level >= 250 -> "화경"
            level >= 200 -> "초절정"
            level >= 150 -> "절정"
            level >= 100 -> "일류"
            level >= 50 -> "이류"
            else -> "삼류"
        }
    }

    fun updateUI(user: User) {
        userNameTextView.text = user.userName
        userHeroTextView.text = user.heroName
        userLvTextView.text = "Lv: ${user.exp?.div(100)}"
        userTitleTextView.text = user.title
        userCoinTextView.text = user.coin.toString()
        userRankTextView.text = "${user.ranking}위"

        progressBarExp.max = 100
        progressBarExp.progress = user.exp?.rem(100) ?: 0

        // character_id에 따라 이미지 설정
        when (user.characterName) {
            "토끼" -> Glide.with(this).load(R.raw.rabbit).into(gifImageView)
            "버섯무리" -> Glide.with(this).load(R.raw.mushroom).into(gifImageView)
            "무사" -> Glide.with(this).load(R.raw.musa).into(gifImageView)
            "아이언맨" -> Glide.with(this).load(R.raw.ironman).into(gifImageView)
            else -> gifImageView.setImageResource(R.raw.default_char)
        }
    }

    private fun updateCurrentUserOnServer(currentUser: User) {
        ApiClient.apiService.updateUser(currentUser.userId!!, currentUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Current user's data updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("MainActivity", "Failed to update current user's data. Response code: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Failed to update current user's data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Failed to connect to server: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRankingDialog(user: User?) {
        if (user == null) {
            Toast.makeText(this, "User data not available", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ranking, null)

        val userNameTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_userNameTextView)
        val heroNameTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_heroNameTextView)
        val levelTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_levelTextView)
        val titleTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_titleTextView)
        val coinTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_coinTextView)
        val ageTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_ageTextView)
        val rankingTextView = dialogView.findViewById<TextView>(R.id.Ranking_dialog_rankingTextView)
        val okButton = dialogView.findViewById<Button>(R.id.Ranking_dialog_okButton)

        userNameTextView.text = "별명: ${user.userName}"
        heroNameTextView.text = "영웅: ${user.heroName}"
        levelTextView.text = "Lv. ${(((user.exp) ?: 0) / 1000)}"
        coinTextView.text = "재화 : ${user.coin}"
        titleTextView.text = "경지: ${user.title}"
        ageTextView.text = "나이: ${user.age}"
        rankingTextView.text = "순위: ${user.ranking}"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        okButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

}
