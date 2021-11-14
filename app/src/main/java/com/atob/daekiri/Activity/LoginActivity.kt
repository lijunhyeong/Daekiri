package com.atob.daekiri.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.atob.daekiri.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var mAuth : FirebaseAuth? = null

    // 이메일 입력
    private val idEditText: EditText by lazy {
        findViewById(R.id.idEditText)
    }

    // 비밀번호 입력
    private val passwordEditText: EditText by lazy {
        findViewById(R.id.passwordEditText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

    }

    // 로그인 버튼
    fun loginButton(v: View){
        val email = idEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "이메일 또는 비밀번호를 입력해주세요 :)", Toast.LENGTH_SHORT).show()
        }else{
            // 로그인
            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                        result ->
                    if(result.isSuccessful){
                        // 유저정보 넘겨주고 로그인 성공 후, MainActivity 이동
                        moveMainPage(mAuth?.currentUser)
                        // 이메일 영구저장
                        val prefs : SharedPreferences = getSharedPreferences("Email", Context.MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = prefs.edit() // 데이터 기록을 위한 editor
                        editor.clear()
                        editor.putString("email", email)
                        editor.apply()

                    }else{
                        Toast.makeText(this, "이메일 또는 비밀번호를 확인해주세요 :)", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    /**
     * 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        moveMainPage(mAuth?.currentUser)
    }*/
    // 유저정보 넘겨주고 로그인 성공 후, MainActivity 이동
    private fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 비밀번호 찾기
    // 이메일을 통해 비밀번호를 재설정합니다. 찾는 것을 불가능합니다.
    // 비밀번호를 찾는 버튼을 생성합니다. -> 새로운 액티비티를 생성합니다.(이메일을 통해 비밀번호를 재설정 할 수 있도록 합니다.) -> 다이얼로그 처리해도 좋을 듯 합니다.
    // 비밀번호 재설정 메소드를 새로운 액티비티에 입력합니다.
    fun findPasswordButton(v: View){

        val findpasswordDialog = layoutInflater.inflate(R.layout.dialog_findpassword, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(findpasswordDialog)
            .create()

        val find = findpasswordDialog.findViewById<EditText>(R.id.editText)
        val success = findpasswordDialog.findViewById<Button>(R.id.successButton)
        val close = findpasswordDialog.findViewById<Button>(R.id.closeButton)

        // 보내기
        success.setOnClickListener {
            if(find.text.toString().isEmpty()){
                Toast.makeText(this, "이메일을 적어주세요.", Toast.LENGTH_SHORT).show()
            }else{
                mAuth?.sendPasswordResetEmail(find.text.toString())
                    ?.addOnCompleteListener {
                            task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "비밀번호 재설정 메일을 보냈습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()

                        }
                    }
                alertDialog.dismiss()
            }


        }

        // 취소
        close.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

    }

    // 회원가입 페이지 이동 버튼
    fun createAccountButton(v: View){
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }


}