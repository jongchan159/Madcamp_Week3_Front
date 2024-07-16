package com.example.project3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingActivity : AppCompatActivity(), RankingAdapter.OnItemClickListener {

    private lateinit var rankingRecyclerView: RecyclerView
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        fetchUsersFromServer()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        // 여기 패치 느리면 thread 붙여야 할 것 같다.

        rankingRecyclerView = findViewById(R.id.ranking_rank_RV)
        rankingRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchUsersFromServer() {
        ApiClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!.sortedByDescending { it.level }

                    // Update ranking based on sorted level
                    for ((index, user) in users.withIndex()) {
                        user.ranking = index + 1
                    }

                    // Send updated ranking to server
                    updateRankingsOnServer(users)

                    val gson = Gson()
                    val jsonResponse = gson.toJson(response.body())
                    Log.d("jangjiwon", "Response: $jsonResponse")

                    // Log each userName to check if they are correctly fetched
                    for (user in users) {
                        Log.d("jangjiwon", "User Name: ${user.userName}")
                    }

                    rankingAdapter = RankingAdapter(users, this@RankingActivity)
                    rankingRecyclerView.adapter = rankingAdapter
                } else {
                    Toast.makeText(this@RankingActivity, "Failed to get users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@RankingActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRankingsOnServer(users: List<User>) {
        ApiClient.apiService.updateUsers(users).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RankingActivity, "Rankings updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RankingActivity, "Failed to update rankings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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

        userNameTextView.text = "Name: ${user.userName}"
        heroNameTextView.text = "Hero Name: ${user.heroName}"
        levelTextView.text = "Level: ${(((user.level)?: 0) /1000)}"
        titleTextView.text = "Title: ${user.title}"
        coinTextView.text = "Coin: ${user.coin}"
        ageTextView.text = "Age: ${user.age}"
        rankingTextView.text = "Ranking: ${user.ranking}"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        okButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
