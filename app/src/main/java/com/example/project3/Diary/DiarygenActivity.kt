package com.example.project3

import android.app.ProgressDialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryGenActivity : AppCompatActivity() {
    private lateinit var etDiaryContent: EditText
    private lateinit var btnSaveDiary: ImageView
    private lateinit var btnCancel: ImageView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diarygen)

        // 상단 바를 투명하게 설정
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        etDiaryContent = findViewById(R.id.et_diary_content)
        btnSaveDiary = findViewById(R.id.button_save)
        btnCancel = findViewById(R.id.button_del)

        btnSaveDiary.setOnClickListener {
            val content = etDiaryContent.text.toString()
            if (content.isNotBlank()) {
                saveDiary(content)
            } else {
                Toast.makeText(this, "Please enter content", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish() // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다.
        }
    }

    private fun saveDiary(content: String) {
        val currentUser = UserHolder.getUser()
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.userId
        if (userId.isNullOrBlank()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            return
        }

        val diary = Diary(
            contents = content,
            userId = userId
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Saving diary...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        ApiClient.apiService.createDiary(diary).enqueue(object : Callback<Diary> {
            override fun onResponse(call: Call<Diary>, response: Response<Diary>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Toast.makeText(this@DiaryGenActivity, "Diary saved!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK) // 성공적으로 저장된 경우 RESULT_OK 설정
                    finish() // 다이어리를 저장한 후 현재 액티비티를 종료합니다.
                } else {
                    Toast.makeText(this@DiaryGenActivity, "Failed to save diary", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Diary>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(this@DiaryGenActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
