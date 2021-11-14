package com.atob.daekiri.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.atob.daekiri.DataClass.DBKey.Companion.USERS
import com.atob.daekiri.DataClass.DBKey.Companion.USER_INFORMATION
import com.atob.daekiri.Fragment.OneFragment
import com.atob.daekiri.Fragment.ThreeFragment
import com.atob.daekiri.Fragment.TwoFragment
import com.atob.daekiri.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val fragmentOne by lazy { OneFragment() }
    private val fragmentTwo by lazy { TwoFragment() }
    private val fragmentThree by lazy { ThreeFragment() }

    // 회원
    private var auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // user 로그인 화면으로
        if (auth == null){
            val loginIntent = Intent(this, LoginActivity::class.java)
            onStartActivity(loginIntent)
        }else{
            // 학생증 인증
            // 회원 목록 가져오기
            var auth: FirebaseAuth= FirebaseAuth.getInstance()
            var userDB: DatabaseReference = Firebase.database.reference.child(USERS)
            val currentUserDB = userDB.child(auth.currentUser?.uid.orEmpty())

            /**
             * 0: 학생증을 확인 중 입니다.
             * 1: 학생증을 다시 입력해주세요.
             * 2: else: Error!
             * 3 : 인증
             */
            currentUserDB.child(USER_INFORMATION).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if ( snapshot.child("studentCard").value == null){
                        val userInfoIntent = Intent(applicationContext, UserInfoActivity::class.java)
                        // 액티비티 스택에 전환되는 액티비티만 존재하여 뒤로가기를 눌렀을 때 반응이 없게 됨
                        onStartActivity(userInfoIntent)
                    }

                    val a = snapshot.child("studentCard").value.toString()
                    val studentPermission: Int = a.toInt()
                    if (studentPermission == 0){
                        val userPermissionIntent = Intent(applicationContext, UserPermissionActivity::class.java)
                        onStartActivity(userPermissionIntent)
                    }else if (studentPermission == 1){
                        val userPermissionRejectionActivity = Intent(applicationContext, UserPermissionRejectionActivity::class.java)
                        onStartActivity(userPermissionRejectionActivity)
                    }else if (studentPermission == 2){
                        // TODO "ERROR!" 개발자에게 문의 부탁드립니다.
                        val userPermissionErrorActivity = Intent(applicationContext, UserPermissionErrorActivity::class.java)
                        startActivity(userPermissionErrorActivity)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }





        // 로그인 성공시
        initNavigationBar()

    }

    private fun initNavigationBar() {
        val bnv_main = findViewById<BottomNavigationView>(R.id.bnv_main)
        bnv_main.run {
            setOnNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.first -> { changeFragment(fragmentOne) }
                    R.id.second -> { changeFragment(fragmentTwo) }
                    R.id.third -> { changeFragment(fragmentThree) }
                }
                true
            }
            selectedItemId = R.id.second
        }
    }


    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, fragment)
            .commit()
    }



    // Intent 함수
    private fun onStartActivity(intent: Intent){
        // 액티비티 스택에 전환되는 액티비티만 존재하여 뒤로가기를 눌렀을 때 반응이 없게 됨
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }




}