package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingActivity : AppCompatActivity(), RankingAdapter.OnItemClickListener {

    private lateinit var rankingRecyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter
    private lateinit var btnCreateClose: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        fetchUsersFromServer()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        rankingRecyclerView = findViewById(R.id.ranking_rank_RV)
        rankingRecyclerView.layoutManager = LinearLayoutManager(this)

        btnCreateClose = findViewById(R.id.diary_button3_BT)
        btnCreateClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchUsersFromServer() {
        ApiClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!.sortedByDescending { it.exp }

                    // Get the current user
                    val currentUser = UserHolder.getUser()

                    // Update ranking based on sorted exp
                    if (currentUser != null) {
                        val currentUserId = currentUser.userId
                        val updatedUsers = users.map { it.copy() }  // 복제하여 새로운 리스트 생성
                        for ((index, user) in updatedUsers.withIndex()) {
                            user.ranking = index + 1
                            if (user.userId == currentUserId) {
                                currentUser.ranking = user.ranking
                            }

                        // Set up RecyclerView
                        rankingAdapter = RankingAdapter(updatedUsers, this@RankingActivity)
                        rankingRecyclerView.adapter = rankingAdapter

                        }

                        // Send updated ranking of current user to server
                        updateCurrentUserRankingOnServer(currentUser)
                    }

                } else {
                    Log.e("RankingActivity", "Failed to get users. Response code: ${response.code()}")
                    Toast.makeText(this@RankingActivity, "Failed to get users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("RankingActivity", "Failed to connect to server: ${t.message}", t)
                Toast.makeText(this@RankingActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCurrentUserRankingOnServer(currentUser: User) {
        ApiClient.apiService.updateUser(currentUser.userId!!, currentUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                } else {
                    Log.e("RankingActivity", "Failed to update current user's ranking. Response code: ${response.code()}")
                    Toast.makeText(this@RankingActivity, "Failed to update current user's ranking", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("RankingActivity", "Failed to connect to server: ${t.message}", t)
                Toast.makeText(this@RankingActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(user: User) {
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
