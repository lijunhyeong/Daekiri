package com.atob.daekiri.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.atob.daekiri.R


class UserPermissionActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_permission)

        var animation = findViewById<LottieAnimationView>(R.id.LottieAnimation)
        animation.setAnimation(R.raw.loading_lottie)
        animation.playAnimation()

    }





}
