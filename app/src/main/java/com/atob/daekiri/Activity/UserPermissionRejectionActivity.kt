package com.atob.daekiri.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.atob.daekiri.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class UserPermissionRejectionActivity : AppCompatActivity() {

    // 카메라, 갤러리 요청
    private val REQUEST_CAMERA = 101
    private val REQUEST_STORAGE = 71

    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private lateinit var studentCardImageViewRejection: ImageView

    // 회원 uid
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_permission_rejection)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        studentCardImageViewRejection = findViewById(R.id.studentCardImageViewRejection)

    }

    // 제출하기
    fun permissionRejectionCheck(v: View) {
        if (filePath == null) {
            Toast.makeText(this, "학생증을 첨부해주세요 :)", Toast.LENGTH_SHORT).show()
        } else {
            // 학생증
            var imgFileName = user?.uid + "_.png"
            var storageReference =
                firebaseStore?.reference?.child("StudentCard")?.child(imgFileName)
            storageReference?.putFile(filePath!!)?.addOnSuccessListener {

            }

            // cloud 초기화, 이외에 데이터 삽입 함수
            val db = Firebase.firestore

            val updateDatabase = db.collection("users").document(user!!.uid)

            updateDatabase.update("studentCard", 0).addOnSuccessListener {
                val userPermissionRejectionIntent =
                    Intent(this, UserPermissionActivity::class.java)
                // 액티비티 스택에 전환되는 액티비티만 존재하여 뒤로가기를 눌렀을 때 반응이 없게 됨
                userPermissionRejectionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                userPermissionRejectionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(userPermissionRejectionIntent)
                Toast.makeText(this, "학생증이 제출되었습니다 :)", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                //Log.w(TAG, "Error updating document", e)
                Toast.makeText(this, "학생증이 제출에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

        }
    }

        // 학생증 넣기
        fun studentCardImageViewRejection(v: View) {
            val studentCardDialog =
                layoutInflater.inflate(R.layout.dialog_studentcard_selection, null)
            val studentCardAlertDialog = AlertDialog.Builder(this)
                .setView(studentCardDialog)
                .create()

            val studentCardCameraDialog: TextView =
                studentCardDialog.findViewById(R.id.dialog_studentcard_camera)
            val studentCardGalleryDialog: TextView =
                studentCardDialog.findViewById(R.id.dialog_studentcard_gallery)
            val studentCardCancelDialog: TextView =
                studentCardDialog.findViewById(R.id.dialog_studentcard_cancel)

            // 카메라
            studentCardCameraDialog.setOnClickListener {
                checkPermission()
                studentCardAlertDialog.dismiss()
            }

            // 겔러리
            studentCardGalleryDialog.setOnClickListener {
                launchGallery()
                studentCardAlertDialog.dismiss()
            }
            // 취소 버튼 클릭시
            studentCardCancelDialog.setOnClickListener { studentCardAlertDialog.dismiss() }

            studentCardAlertDialog.show()
        }



        // 카메라
        //권한 확인
        fun checkPermission() {

            // 1. 위험권한(Camera) 권한 승인상태 가져오기
            val cameraPermission =
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                // 카메라 권한이 승인된 상태일 경우
                startProcess()

            } else {
                // 카메라 권한이 승인되지 않았을 경우
                requestPermission()
            }
        }

        // 2. 권한 요청
        private fun requestPermission() {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA)
        }

        // 권한 처리
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                REQUEST_CAMERA -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startProcess()
                    } else {
                        Log.d("MainActivity", "종료")
                    }
                }
            }
        }

        // 3. 카메라 기능 실행
        fun startProcess() {
            launchCamera()
        }

        private fun launchCamera() {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }

        // 갤러리
        private fun launchGallery() {
            val photoPickIntent = Intent(Intent.ACTION_PICK)
            photoPickIntent.type = "image/*"
            startActivityForResult(photoPickIntent, REQUEST_STORAGE)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            var studentCardImageView = findViewById<ImageView>(R.id.studentCardImageView)

            // 갤러리
            if (requestCode == REQUEST_STORAGE && resultCode == RESULT_OK) {
                if (data == null || data.data == null) {
                    return
                }
                filePath = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    studentCardImageViewRejection.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            } else if (requestCode == REQUEST_CAMERA) {
                //카메라로 방금 촬영한 이미지를 미리 만들어 놓은 이미지뷰로 전달 합니다.
                val bitmap = data?.extras?.get("data") as Bitmap
                studentCardImageViewRejection.setImageBitmap(bitmap)
                filePath = getImageUri(this, bitmap)
            }
        }

        // 카메라로 찍은 bitmap을 uril로 변환
        private fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
            val bytes = ByteArrayOutputStream()
            if (inImage != null) {
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            }
            val path = MediaStore.Images.Media.insertImage(inContext?.getContentResolver(),
                inImage,
                "Title" + " - " + Calendar.getInstance().getTime(),
                null)
            return Uri.parse(path)
        }



}
