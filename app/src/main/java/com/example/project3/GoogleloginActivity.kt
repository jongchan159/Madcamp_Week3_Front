package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleloginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: ImageView

    private val RC_SIGN_IN = 1

    data class UserResponse(
        val success: Boolean,
        val user: User?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlelogin)

        signInButton = findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = android.graphics.Color.TRANSPARENT
        }

        // GoogleSignInOptions 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()  // ID 요청 추가
            .build()

        // GoogleSignInClient 초기화
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // 구글 로그인 성공 처리
            Toast.makeText(this, "반갑습니다 ${account?.displayName}님!", Toast.LENGTH_SHORT).show()

            // ID를 가져와서 사용
            val id = account?.id
            Log.d("GoogleSignIn", "Google 로그인 성공 - ID: $id")

            if (id != null) {
                checkUserOnServer(id)
            } else {
                Log.e("GoogleSignIn", "Google 로그인 실패 - ID가 null입니다.")
            }
        } catch (e: ApiException) {
            // 구글 로그인 실패 처리
            Log.e("GoogleSignIn", "Google 로그인 실패: ${e.message}")
            Toast.makeText(this, "구글 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUserOnServer(id: String) {
        Log.d("GoogleSignIn", "서버에 사용자 정보 확인 요청 - ID: $id")
        ApiClient.apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    Log.d("GoogleSignIn", "서버 응답 성공 - 사용자 목록: $users")

                    val user = users?.find { it.userId.toString() == id }

                    if (user != null) {
                        Log.d("GoogleSignIn", "서버에서 사용자 정보 확인 - 유저 존재: $user")
                        // 유저 정보가 존재하면 UserHolder에 설정
                        UserHolder.setUser(user)
                        // MainActivity로 이동
                        val intent = Intent(this@GoogleloginActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("GoogleSignIn", "서버에서 사용자 정보 확인 - 유저가 존재하지 않음")
                        // 유저 정보가 없으면 RegisterActivity로 이동
                        val newUser = User(userId = id) // User 객체에 userId 설정
                        UserHolder.setUser(newUser)
                        val intent = Intent(this@GoogleloginActivity, RegisterActivity::class.java).apply {
                            putExtra("USER_ID", id) // 인텐트에 userId 추가
                        }
                        startActivity(intent)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("GoogleSignIn", "서버 응답 오류 - 에러 본문: $errorBody")
                    Toast.makeText(this@GoogleloginActivity, "서버 에러", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("GoogleSignIn", "서버 요청 실패: ${t.message}")
                Toast.makeText(this@GoogleloginActivity, "서버 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
