// DiaryActivity.kt
package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryActivity : AppCompatActivity() {
    private lateinit var questRecyclerView: RecyclerView
    private lateinit var questAdapter: QuestAdapter
    private lateinit var diaryRecyclerView: RecyclerView
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var btnCreateDiary: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // 예제 User 객체 설정
        val user = User(
            userId = "1001010110111",
            userName = "장지원",
            heroName = "hero1",
            level = 5,
            title = "Novice",
            coin = 100,
            age = 25,
            ranking = 1,
            backgroundId = 101,
            characterId = 202,
        )

        // UserHolder에 사용자 설정
        UserHolder.setUser(user)

        // RecyclerView 설정
        questRecyclerView = findViewById(R.id.diary_list1_RV)
        questRecyclerView.layoutManager = LinearLayoutManager(this)
        diaryRecyclerView = findViewById(R.id.diary_list2_RV)
        diaryRecyclerView.layoutManager = LinearLayoutManager(this)

        // 버튼 설정 및 클릭 이벤트 추가
        btnCreateDiary = findViewById(R.id.diary_button2_BT)
        btnCreateDiary.setOnClickListener {
//            val intent = Intent(this, DiaryGenActivity::class.java)
//            startActivity(intent)
            updateAllUsersQuests()

        }

        // 다이어리 데이터 가져오기
        fetchDiaries()
        fetchQuests()
    }

    private fun fetchDiaries() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            Log.e("jangjiwon", "Current user is null")
            return
        }

        val currentUserId = currentUser.userId
        if (currentUserId == null) {
            Log.e("jangjiwon", "Current user ID is not a valid integer")
            return
        }

        ApiClient.apiService.getDiaries().enqueue(object : Callback<List<Diary>> {
            override fun onResponse(call: Call<List<Diary>>, response: Response<List<Diary>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allDiaries = response.body()!!
                    Log.d("jangjiwon", "Fetched ${allDiaries.size} diaries")

                    val userDiaries = allDiaries.filter {
                        Log.d("jangjiwon", "Diary UserId: ${it.userId}, Current UserId: $currentUserId")
                        it.userId == currentUserId
                    }
                    diaryAdapter = DiaryAdapter(userDiaries) { diary ->
                        showDiaryDialog(diary)
                    }
                    diaryRecyclerView.adapter = diaryAdapter
                } else {
                    Log.e("jangjiwon", "Response not successful: ${response.code()} - ${response.message()}")
                    Log.e("jangjiwon", "Response body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Diary>>, t: Throwable) {
                Log.e("jangjiwon", "Failed to fetch diaries", t)
            }
        })
    }

    private fun fetchQuests() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            Log.e("jangjiwon", "Current user is null")
            return
        }

        ApiClient.apiService.getQuests().enqueue(object : Callback<List<Quest>> {
            override fun onResponse(call: Call<List<Quest>>, response: Response<List<Quest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allQuests = response.body()!!
                    Log.d("jangjiwon", "Fetched ${allQuests.size} quests")

                    val userQuests = allQuests.filter {
                        Log.d("jangjiwon", "Quest UserId: ${it.userId}, Current UserId: ${currentUser.userId}")
                        it.userId == currentUser.userId
                    }
                    questAdapter = QuestAdapter(userQuests) { quest ->
                        showQuestDialog(quest)
                    }
                    questRecyclerView.adapter = questAdapter
                } else {
                    Log.e("jangjiwon", "Response not successful: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Quest>>, t: Throwable) {
                Log.e("jangjiwon", "Failed to fetch quests", t)
            }
        })
    }

    private fun showDiaryDialog(diary: Diary) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Diary Details")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun showQuestDialog(quest: Quest) {
        val dialogView = layoutInflater.inflate(R.layout.quest_dialog, null)
        val questTitle: TextView = dialogView.findViewById(R.id.quest_title)
        val questDetails: TextView = dialogView.findViewById(R.id.quest_details)
        val progressBar: ProgressBar = dialogView.findViewById(R.id.progress_bar)
        val timeRemaining: TextView = dialogView.findViewById(R.id.time_remaining)
        val startButton: Button = dialogView.findViewById(R.id.start_button)
        val stopButton: Button = dialogView.findViewById(R.id.stop_button)

        questTitle.text = "Quest ID: ${quest.questId}"
        questDetails.text = """
        Title: ${quest.contents}
        Type: ${quest.type}
        Completion Status: ${if (quest.isComplete) "완료" else "미완료"}
    """.trimIndent()

        val completeTimeInSeconds = 1800 // 30 minutes
        val handler = Handler(Looper.getMainLooper())
        var startTime = System.currentTimeMillis()
        var isRunning = false

        fun updateProgressTimeOnServer(progressTime: Int) {
            quest.progressTime = progressTime.toString()
            ApiClient.apiService.updateQuestProgress(quest.questId, quest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("DiaryActivity", "Progress time updated successfully")
                    } else {
                        Log.e("DiaryActivity", "Failed to update progress time: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("DiaryActivity", "Error updating progress time", t)
                }
            })
        }

        val updateProgressBar = object : Runnable {
            override fun run() {
                if (!isRunning) return

                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                progressBar.progress = elapsedTime.toInt()

                val remainingTime = completeTimeInSeconds - elapsedTime
                val minutes = remainingTime / 60
                val seconds = remainingTime % 60
                timeRemaining.text = String.format("Time Remaining: %02d:%02d", minutes, seconds)

                if (elapsedTime < completeTimeInSeconds) {
                    handler.postDelayed(this, 1000)
                } else {
                    quest.isComplete = true
                    questDetails.text = questDetails.text.toString() + "\nCompletion Time: ${quest.completeTime}"
                    progressBar.progress = progressBar.max
                    isRunning = false
                    updateProgressTimeOnServer(progressBar.max)
                }

                updateProgressTimeOnServer(elapsedTime.toInt())
            }
        }

        startButton.setOnClickListener {
            if (!isRunning) {
                startTime = System.currentTimeMillis() - (progressBar.progress * 1000)
                isRunning = true
                handler.post(updateProgressBar)
            }
        }

        stopButton.setOnClickListener {
            isRunning = false
            updateProgressTimeOnServer(progressBar.progress)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Quest Details")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                isRunning = false
                updateProgressTimeOnServer(progressBar.progress)
            }
            .create()

        // 다이얼로그가 열릴 때 서버에서 진행 시간 가져오기
        ApiClient.apiService.getQuestProgress(quest.questId).enqueue(object : Callback<Quest> {
            override fun onResponse(call: Call<Quest>, response: Response<Quest>) {
                if (response.isSuccessful && response.body() != null) {
                    val fetchedQuest = response.body()!!
                    progressBar.progress = fetchedQuest.progressTime?.toInt() ?: 0
                    startTime = System.currentTimeMillis() - (progressBar.progress * 1000)
                    if (progressBar.progress >= completeTimeInSeconds) {
                        quest.isComplete = true
                        questDetails.text = questDetails.text.toString() + "\nCompletion Time: ${quest.completeTime}"
                        progressBar.progress = progressBar.max
                    }
                } else {
                    Log.e("DiaryActivity", "Failed to fetch progress time: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Quest>, t: Throwable) {
                Log.e("DiaryActivity", "Error fetching progress time", t)
            }
        })

        dialog.show()
    }

    private fun updateAllUsersQuests() {
        ApiClient.apiService.updateAllUsersQuests().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DiaryActivity", "Successfully requested quest generation for all users")
                } else {
                    Log.e("DiaryActivity", "Failed to request quest generation: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DiaryActivity", "Request to generate quests failed", t)
            }
        })
    }
}
