package com.example.project3

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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
    private lateinit var btnCreateDiary: ImageView
    private lateinit var btnCreateToDo: ImageView

    companion object {
        const val REQUEST_CODE_CREATE_DIARY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        // RecyclerView 설정
        questRecyclerView = findViewById(R.id.diary_list1_RV)
        questRecyclerView.layoutManager = LinearLayoutManager(this)
        diaryRecyclerView = findViewById(R.id.diary_list2_RV)
        diaryRecyclerView.layoutManager = LinearLayoutManager(this)

        // 버튼 설정 및 클릭 이벤트 추가
        btnCreateDiary = findViewById(R.id.diary_button2_BT)
        btnCreateDiary.setOnClickListener {
            val intent = Intent(this, DiaryGenActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CREATE_DIARY)
        }

        btnCreateToDo = findViewById(R.id.diary_button1_BT)
        btnCreateToDo.setOnClickListener {
            updateCurrentUserQuests()
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

                    // 최신 세 개의 퀘스트만 가져오기
                    val topThreeQuests = userQuests.takeLast(3)

                    questAdapter = QuestAdapter(topThreeQuests) { quest ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_DIARY && resultCode == RESULT_OK) {
            // 다이어리 데이터 갱신
            fetchDiaries()
        }
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

        var startTime: Long = 0
        var isRunning = false
        var elapsedTimeInSeconds: Int = 0
        val completeTimeInSeconds = 1800 // 30 minutes

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

        val handler = Handler(Looper.getMainLooper())
        val updateRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    val currentTime = System.currentTimeMillis()
                    val timeDiff = (currentTime - startTime) / 1000
                    elapsedTimeInSeconds = timeDiff.toInt()

                    progressBar.progress = elapsedTimeInSeconds
                    val remainingTimeInSeconds = completeTimeInSeconds - elapsedTimeInSeconds
                    val minutes = remainingTimeInSeconds / 60
                    val seconds = remainingTimeInSeconds % 60
                    timeRemaining.text = String.format("Time Remaining: %02d:%02d", minutes, seconds)

                    if (elapsedTimeInSeconds >= completeTimeInSeconds) {
                        quest.isComplete = true
                        questDetails.text = questDetails.text.toString() + "\nCompletion Time: ${quest.completeTime}"
                        progressBar.progress = progressBar.max
                        isRunning = false
                        updateProgressTimeOnServer(completeTimeInSeconds)
                    }

                    handler.postDelayed(this, 1000)
                }
            }
        }

        startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                startTime = System.currentTimeMillis() - (elapsedTimeInSeconds * 1000).toLong()
                handler.post(updateRunnable)
            }
        }

        stopButton.setOnClickListener {
            if (isRunning) {
                isRunning = false
                handler.removeCallbacks(updateRunnable)
                updateProgressTimeOnServer(elapsedTimeInSeconds)
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Quest Details")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                if (isRunning) {
                    handler.removeCallbacks(updateRunnable)
                    updateProgressTimeOnServer(elapsedTimeInSeconds)
                }
            }
            .create()

        // 다이얼로그가 열릴 때 서버에서 진행 시간 가져오기
        ApiClient.apiService.getQuestProgress(quest.questId).enqueue(object : Callback<Quest> {
            override fun onResponse(call: Call<Quest>, response: Response<Quest>) {
                if (response.isSuccessful && response.body() != null) {
                    val fetchedQuest = response.body()!!
                    // Convert progress time from HH:mm:ss to total seconds
                    val progressTime = calculateTotalSecondsFromString(fetchedQuest.progressTime ?: "00:00:00")
                    progressBar.progress = progressTime
                    elapsedTimeInSeconds = progressTime
                    startTime = System.currentTimeMillis() - (elapsedTimeInSeconds * 1000).toLong()
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

    // Function to calculate total seconds from HH:mm:ss formatted string
    private fun calculateTotalSecondsFromString(timeString: String): Int {
        val parts = timeString.split(":")
        if (parts.size != 3) return 0

        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        val seconds = parts[2].toIntOrNull() ?: 0

        return hours * 3600 + minutes * 60 + seconds
    }

    private fun updateCurrentUserQuests() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating quests...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            progressDialog.dismiss()
            Log.e("jangjiwon", "Current user is null")
            return
        }

        val userId = currentUser.userId
        if (userId.isNullOrBlank()) {
            progressDialog.dismiss()
            Log.e("jangjiwon", "Invalid user ID")
            return
        }

        ApiClient.apiService.updateUserQuests(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.d("DiaryActivity", "Successfully requested quest generation for user $userId")
                    fetchDiaries() // Fetch updated diaries
                    fetchQuests() // Fetch updated quests
                } else {
                    Log.e("DiaryActivity", "Failed to request quest generation: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                progressDialog.dismiss()
                Log.e("DiaryActivity", "Request to generate quests failed", t)
            }
        })
    }
}
