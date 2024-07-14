package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
            userId = 1,
            userName = "장지원",
            heroName = "hero1",
            level = 5,
            title = "Novice",
            coin = 100,
            age = 25,
            ranking = 1,
            backgroundId = 101,
            characterId = 202
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
            val intent = Intent(this, DiaryGenActivity::class.java)
            startActivity(intent)
        }

        // 다이어리 데이터 가져오기
        fetchDiaries()
        fetchQuests()
    }

    private fun fetchDiaries() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            // Handle the case where the user is not set
            Log.e("jangjiwon", "Current user is null")
            return
        }

        ApiClient.apiService.getDiaries().enqueue(object : Callback<List<Diary>> {
            override fun onResponse(call: Call<List<Diary>>, response: Response<List<Diary>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allDiaries = response.body()!!
                    Log.d("jangjiwon", "Fetched ${allDiaries.size} diaries")

                    // 현재 사용자와 일치하는 다이어리만 필터링
                    val userDiaries = allDiaries.filter {
                        Log.d("jangjiwon", "Diary UserId: ${it.userId}, Current UserId: ${currentUser.userId}")
                        it.userId == currentUser.userId
                    }
                    diaryAdapter = DiaryAdapter(userDiaries)
                    diaryRecyclerView.adapter = diaryAdapter
                } else {
                    Log.e("jangjiwon", "Response not successful: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Diary>>, t: Throwable) {
                // Handle failure
                Log.e("jangjiwon", "Failed to fetch diaries", t)
            }
        })
    }

    private fun fetchQuests() {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            // Handle the case where the user is not set
            Log.e("jangjiwon", "Current user is null")
            return
        }

        ApiClient.apiService.getQuests().enqueue(object : Callback<List<Quest>> {
            override fun onResponse(call: Call<List<Quest>>, response: Response<List<Quest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val allQuests = response.body()!!
                    Log.d("jangjiwon", "Fetched ${allQuests.size} quests")

                    // 현재 사용자와 일치하는 퀘스트만 필터링
                    val userQuests = allQuests.filter {
                        Log.d("jangjiwon", "Quest UserId: ${it.userId}, Current UserId: ${currentUser.userId}")
                        it.userId == currentUser.userId
                    }
                    questAdapter = QuestAdapter(userQuests)
                    questRecyclerView.adapter = questAdapter
                } else {
                    Log.e("jangjiwon", "Response not successful: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Quest>>, t: Throwable) {
                // Handle failure
                Log.e("jangjiwon", "Failed to fetch quests", t)
            }
        })
    }
}
