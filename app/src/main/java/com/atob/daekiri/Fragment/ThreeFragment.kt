package com.atob.daekiri.Fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.atob.daekiri.Activity.ProfileModificationActivity
import com.atob.daekiri.R
import com.atob.daekiri.databinding.FragmentThreeBinding

class ThreeFragment : Fragment(R.layout.fragment_three) {

    private var binding: FragmentThreeBinding?= null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragmentThreeBinding = FragmentThreeBinding.bind(view)
        binding = fragmentThreeBinding

        // profile 수정 페이지로 이동
        binding!!.editProfileThree.setOnClickListener {
            var profileModificationIntent = Intent(context, ProfileModificationActivity::class.java)
            startActivity(profileModificationIntent)
        }

        // 프로필 이미지 크기 구하기
        profileImageViewSize()
    }


    // 프로필 이미지 크기 구하기
    @RequiresApi(Build.VERSION_CODES.R)
    private fun profileImageViewSize() {

        // 프로필 이미지 선언
        val imageThree = view?.findViewById<ImageView>(R.id.ImageThree)

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        try {
            requireContext().display?.getRealMetrics(displayMetrics)
        } catch (e: NoSuchMethodError) {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }

        imageThree!!.layoutParams.width = displayMetrics.widthPixels
        imageThree.layoutParams.height = displayMetrics.widthPixels

    }



}