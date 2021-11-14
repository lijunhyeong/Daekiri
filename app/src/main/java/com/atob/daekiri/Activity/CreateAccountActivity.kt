package com.atob.daekiri.Activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atob.daekiri.R
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    // 이메일 입력
    private val emailEditText: EditText by lazy {
        findViewById(R.id.emailEditText)
    }

    /*
    // 닉네임 입력
    private val nickNameEditText: EditText by lazy {
        findViewById(R.id.nickNameEditText)
    }

    // 대학교 입력
    private val universityEditText: EditText by lazy {
        findViewById(R.id.universityEditText)
    }

    // 전공 입력
    private val majorEditText: EditText by lazy {
        findViewById(R.id.majorEditText)
    }
     */

    // 비밀번호 입력
    private val passwordCheckEditText: EditText by lazy {
        findViewById(R.id.passwordCheckEditText)
    }

    // 비밀번호 확인 입력
    private val passwordCheckEditText2: EditText by lazy {
        findViewById(R.id.passwordCheckEditText2)
    }

    private var mAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        mAuth = FirebaseAuth.getInstance()

    }


    // 회원가입 완료 버튼
    fun createAccountCompleted(v:View){

        val email = emailEditText.text.toString()
        val password = passwordCheckEditText.text.toString()
        val password2 = passwordCheckEditText2.text.toString()

        if (email.isEmpty() || password.isEmpty() || password2.isEmpty()){
            Toast.makeText(this, "이메일 또는 비밀번호를 입력해주세요 :)", Toast.LENGTH_SHORT).show()
        }else if (password != password2){
            Toast.makeText(this, "비밀번호를 확인해주세요 :)", Toast.LENGTH_SHORT).show()
        }else{
            // 회원가입
            mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener{ result ->
                    if (result.isSuccessful){
                        Toast.makeText(this, "회원가입이 완료되었습니다 :)", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this,"오류 발생",Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


}