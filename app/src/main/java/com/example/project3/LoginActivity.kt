package com.example.project3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project3.databinding.ActivityLoginBinding
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.NaverIdLoginSDK.oauthLoginCallback
import com.navercorp.nid.oauth.OAuthLoginCallback

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewBinding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NaverIdLoginSDK.initialize(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.naver_client_name)
        )

        // 로그인 버튼에 클릭 리스너 설정
        binding.buttonOAuthLoginImg.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 로그인 성공 처리
                    val accessToken = NaverIdLoginSDK.getAccessToken()
                    Toast.makeText(this@LoginActivity, "로그인 성공! 토큰: $accessToken", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    // 로그인 실패 처리
                    Toast.makeText(this@LoginActivity, "로그인 실패: $message (Error Code: $httpStatus)", Toast.LENGTH_SHORT).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    // 로그인 에러 처리
                    Toast.makeText(this@LoginActivity, "로그인 에러: $message (Error Code: $errorCode)", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }




}