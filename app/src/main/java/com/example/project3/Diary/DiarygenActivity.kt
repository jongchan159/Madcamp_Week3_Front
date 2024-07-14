package com.example.project3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DiaryGenActivity : AppCompatActivity() {
    private lateinit var etDiaryTitle: EditText
    private lateinit var etDiaryContent: EditText
    private lateinit var btnSaveDiary: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diarygen)

        etDiaryTitle = findViewById(R.id.et_diary_title)
        etDiaryContent = findViewById(R.id.et_diary_content)
        btnSaveDiary = findViewById(R.id.btn_save_diary)
        btnCancel = findViewById(R.id.btn_cancel)

        btnSaveDiary.setOnClickListener {
            val title = etDiaryTitle.text.toString()
            val content = etDiaryContent.text.toString()
            if (title.isNotBlank() && content.isNotBlank()) {
                saveDiary(title, content)
            } else {
                Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish() // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다.
        }
    }

    private fun saveDiary(title: String, content: String) {
        // 다이어리를 저장하는 로직을 추가합니다.
        // 예: 서버로 다이어리 데이터를 보내거나 로컬 데이터베이스에 저장합니다.
        Toast.makeText(this, "Diary saved!", Toast.LENGTH_SHORT).show()
        finish() // 다이어리를 저장한 후 현재 액티비티를 종료합니다.
    }
}
