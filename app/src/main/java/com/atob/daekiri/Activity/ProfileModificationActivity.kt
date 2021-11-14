package com.atob.daekiri.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.atob.daekiri.R
import com.google.firebase.auth.FirebaseAuth

class ProfileModificationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modification)

    }




    // 로그아웃
    fun logOut(v: View){
        // 로그인 화면으로
        val intent = Intent(this, LoginActivity::class.java)
        // 액티비티 스택에 전환되는 액티비티만 존재하여 뒤로가기를 눌렀을 때 반응이 없게 됨
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        FirebaseAuth.getInstance().signOut()

        /*    private var mAuth : FirebaseAuth? = null
                mAuth = FirebaseAuth.getInstance()
        mAuth?.signOut()*/
    }


}