package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etHeroName: EditText
    private lateinit var etAge: EditText
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        etUserName = findViewById(R.id.etUserName)
        etHeroName = findViewById(R.id.etHeroName)
        etAge = findViewById(R.id.etAge)
        btnSignUp = findViewById(R.id.btnSignUp)

        val userId = intent.getStringExtra("USER_ID") ?: ""

        btnSignUp.setOnClickListener {
            val userName = etUserName.text.toString().trim()
            val heroName = etHeroName.text.toString().trim()
            val age = etAge.text.toString().toIntOrNull() ?: 0

            if (userName.isNotEmpty() && heroName.isNotEmpty() && age > 0) {
                // 기존 사용자 가져오기
                val user = UserHolder.getUser() ?: User(
                    userId = userId,
                    userName = userName,
                    heroName = heroName,
                    age = age,
                    level = 1, // Default values
                    title = "삼류",
                    coin = 0,
                    ranking = 0,
                    backgroundId = null,
                    characterName = "default_char"
                )

                // 사용자 정보 업데이트
                user.userId = userId
                user.userName = userName
                user.heroName = heroName
                user.age = age
                user.title = "삼류"

                // UserHolder에 업데이트된 사용자 저장
                UserHolder.setUser(user)
                Log.d("RegisterActivity", "User set: ${UserHolder.getUser()}")

                // 서버에 사용자 정보 전송
                signUpUser(user)
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(user: User) {
        // 서버에 사용자 데이터 전송
        val call = ApiClient.apiService.createUser(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("RegisterActivity", "Sign Up Failed: $errorMessage")
                    Toast.makeText(this@RegisterActivity, "Sign Up Failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("RegisterActivity", "Sign Up Request Failed", t)
                Toast.makeText(this@RegisterActivity, "Sign Up Request Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
