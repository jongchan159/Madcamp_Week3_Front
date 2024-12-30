package com.example.project3

import DividerItemDecoration
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
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
    private lateinit var btnCreateClose: ImageView

    companion object {
        const val REQUEST_CODE_CREATE_DIARY = 1
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        val questDescription = findViewById<TextView>(R.id.quest_description_TV)
        questDescription.text = """
            퀘스트는 한번에 다섯개 생성됩니다.
            의뢰를 하면 모든 퀘스트가 초기화됩니다.
        """.trimIndent()

        // RecyclerView 설정
        questRecyclerView = findViewById(R.id.quest_list_RV)
        questRecyclerView.layoutManager = LinearLayoutManager(this)
//        diaryRecyclerView = findViewById(R.id.diary_list2_RV)
//        diaryRecyclerView.layoutManager = LinearLayoutManager(this)

        // 다이어리 구분선 추가
        val dividerDrawable: Drawable? = ContextCompat.getDrawable(this, R.drawable.divider)
        if (dividerDrawable != null) {
            val itemDecoration = DividerItemDecoration(dividerDrawable)
//            diaryRecyclerView.addItemDecoration(itemDecoration)
            questRecyclerView.addItemDecoration(itemDecoration)
        }

        // 버튼 설정 및 클릭 이벤트 추가
//        btnCreateDiary = findViewById(R.id.diary_button2_BT)
//        btnCreateDiary.setOnClickListener {
//            val intent = Intent(this, DiaryGenActivity::class.java)
//            startActivityForResult(intent, REQUEST_CODE_CREATE_DIARY)
//        }

        btnCreateToDo = findViewById(R.id.diary_generate_BT)
        btnCreateToDo.setOnClickListener {
            updateCurrentUserQuests()
            fetchQuests()
        }

        btnCreateClose = findViewById(R.id.diary_close_BT)
        btnCreateClose.setOnClickListener {
            finish()
        }

        // 다이어리 데이터 가져오기
//        fetchDiaries()
        fetchQuests()
    }

    private fun fetchDiaries() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            Log.e("quest", "Current user is null")
            return
        }

        val currentUserId = currentUser.userId
        if (currentUserId == null) {
            Log.e("quest", "Current user ID is not a valid integer")
            return
        }

        ApiClient.apiService.getDiaries().enqueue(object : Callback<List<Diary>> {
            override fun onResponse(call: Call<List<Diary>>, response: Response<List<Diary>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allDiaries = response.body()!!
                    Log.d("quest", "Fetched ${allDiaries.size} diaries")

                    val userDiaries = allDiaries.filter {
                        Log.d("quest", "Diary UserId: ${it.userId}, Current UserId: $currentUserId")
                        it.userId == currentUserId
                    }
//                    diaryAdapter = DiaryAdapter(userDiaries) { diary ->
//                        showDiaryDialog(diary)
//                    }
//                    diaryRecyclerView.adapter = diaryAdapter
                } else {
                    Log.e("diary", "Response not successful: ${response.code()} - ${response.message()}")
                    Log.e("diary", "Response body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Diary>>, t: Throwable) {
                Log.e("diary", "Failed to fetch diaries", t)
            }
        })
    }

    private fun fetchQuests() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            Log.e("fetchQuest", "Current user is null")
            return
        }

        ApiClient.apiService.getQuests().enqueue(object : Callback<List<Quest>> {
            override fun onResponse(call: Call<List<Quest>>, response: Response<List<Quest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allQuests = response.body()!!
                    Log.d("QuestResponse", "Fetched ${allQuests.size} quests")

                    val userQuests = allQuests.filter {
                        Log.d("QuestResponse", "Quest UserId: ${it.userId}, Current UserId: ${currentUser.userId}")
                        it.userId == currentUser.userId
                    }

                    // 최신 5 개의 퀘스트만 가져오기
                    val topFiveQuests = userQuests.takeLast(5)

                    questAdapter = QuestAdapter(topFiveQuests) { quest ->
                        showQuestDialog(quest)
                        Log.i("QuestAdapterDialog", quest.contents)
                    }
                    questRecyclerView.adapter = questAdapter
                } else {
                    Log.e("QuestResponse", "Response not successful: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Quest>>, t: Throwable) {
                Log.e("QuestFailure", "Failed to fetch quests", t)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_DIARY && resultCode == RESULT_OK) {
            // 다이어리 데이터 갱신
//            fetchDiaries()
            fetchQuests()
        }
    }

    private fun showDiaryDialog(diary: Diary) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Diary Details")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showQuestDialog(quest: Quest) {
        // Duration으로 오는 값을 퀘스트 계산을 위해 초단위로 반환
        fun parseDurationToSeconds(duration: String): Int {
            // "시간:분:초" 형식의 문자열을 파싱
            val timeParts = duration.split(":")
            if (timeParts.size != 3) return 0 // 잘못된 형식일 경우 기본값 0 반환

            val hours = timeParts[0].toIntOrNull() ?: 0
            val minutes = timeParts[1].toIntOrNull() ?: 0
            val seconds = timeParts[2].toIntOrNull() ?: 0

            // 총 초 계산
            return (hours * 3600) + (minutes * 60) + seconds
        }

        val dialogView = layoutInflater.inflate(R.layout.quest_dialog, null)
        val questTitle: TextView = dialogView.findViewById(R.id.quest_title)
        val questDetails: TextView = dialogView.findViewById(R.id.quest_type)
        val questStatus: TextView = dialogView.findViewById(R.id.quest_status)
        val progressBar: ProgressBar = dialogView.findViewById(R.id.progress_bar)
        val timeRemaining: TextView = dialogView.findViewById(R.id.time_remaining)
        val startButton: Button = dialogView.findViewById(R.id.start_button)
        val stopButton: Button = dialogView.findViewById(R.id.stop_button)

        questTitle.text = "${quest.contents}"
        Log.i("QuestDialog", questDetails.toString())
        questDetails.text = "의뢰 분야: ${quest.type}"
        questStatus.text = "의뢰 상태: ${if (quest.isComplete) "완료!" else "미완료"}"

        var startTime: Long = 0
        var isRunning = false
        var elapsedTimeInSeconds: Int = 0
        val completeTimeInSeconds = parseDurationToSeconds(quest.completeTime.toString())
        Log.i("questDialog completeTime", "${questTitle.text} complete time in text: ${quest.completeTime.toString()}")
        Log.i("questDialog completeTime", "${questTitle.text} complete time in second: ${completeTimeInSeconds}")

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

        fun updateUserExp() {
            val currentUser = UserHolder.getUser()
            if (currentUser != null) {
                currentUser.exp = (currentUser.exp ?: 0) + 10
                currentUser.coin = currentUser.coin?.plus(100)
                Log.d("questUpdate", "Updated experience: ${currentUser.exp}")

                ApiClient.apiService.updateUser(currentUser.userId!!, currentUser).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Log.d("questUpdate", "User experience updated successfully")
                        } else {
                            Log.e("questUpdate", "Failed to update user experience: ${response.code()} - ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("DiaryActivity", "Error updating user experience", t)
                    }
                })
            }
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
                    timeRemaining.text = String.format("남은 시간: %02d:%02d", minutes, seconds)

                    if (elapsedTimeInSeconds >= completeTimeInSeconds) {
                        quest.isComplete = true
                        questStatus.text = "의뢰 상태: 완료!"
                        progressBar.progress = progressBar.max
                        Toast.makeText(this@DiaryActivity, "퀘스트 완료! 10의 경험치와 100 금화를 획득합니다.", Toast.LENGTH_SHORT).show()
                        isRunning = false
                        updateProgressTimeOnServer(completeTimeInSeconds)
                        updateUserExp()
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

        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
/*            .setTitle("Quest Details")*/
            .setView(dialogView)
/*            .setPositiveButton("OK") { _, _ ->*//*
                if (isRunning) {
                    handler.removeCallbacks(updateRunnable)
                    updateProgressTimeOnServer(elapsedTimeInSeconds)
                }*//*
            }*/
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
            Log.e("update quest dialog", "Current user is null")
            return
        }

        val userId = currentUser.userId
        if (userId.isNullOrBlank()) {
            progressDialog.dismiss()
            Log.e("update quest dialog", "Invalid user ID")
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
