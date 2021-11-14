package com.atob.daekiri.Activity

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.atob.daekiri.DataClass.DBKey
import com.atob.daekiri.DataClass.DBKey.Companion.USER_INFORMATION
import com.atob.daekiri.R
import com.atob.daekiri.DataClass.UserInfo
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class UserInfoActivity : AppCompatActivity() {

    // 카메라, 갤러리 요청
    private val REQUEST_CAMERA = 101
    private val REQUEST_STORAGE = 71

    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    // 전역 선언
    private var userNickName: EditText?=null
    private var universityName: EditText?=null
    private var majorName: EditText?=null
    private var entranceYear: TextView?=null    // 물음표를 붙이면 null을 담을 수 있다.
    private var userAge: TextView?=null

    // 재학, 휴학
    private var State: String?=null
    private lateinit var accountInSchool: AppCompatButton
    private lateinit var accountOffFromSchool: AppCompatButton

    // 회원 uid
    private val user = FirebaseAuth.getInstance().currentUser

    // 배울래, 알려줄래, 함께할래
    private var learnSelectionArrayList:ArrayList<String> = ArrayList()
    private var teachSelectionArrayList:ArrayList<String> = ArrayList()
    private var mateSelectionArrayList:ArrayList<String> = ArrayList()

    // 성별
    private var gender: Int?=null
    private lateinit var userMale:ImageView
    private lateinit var userFemale:ImageView

    // 빈 배열
    private var selectionArrayList:ArrayList<String> = ArrayList()

    // 배울래 아이템
    private lateinit var userInfoLearnItem1: TextView
    private lateinit var userInfoLearnItem2: TextView
    private lateinit var userInfoLearnItem3: TextView

    // 알려줄래 아이템
    private lateinit var userInfoTeachItem1: TextView
    private lateinit var userInfoTeachItem2: TextView
    private lateinit var userInfoTeachItem3: TextView

    // 함께할래 아이템
    private lateinit var userInfoMateItem1: TextView
    private lateinit var userInfoMateItem2: TextView
    private lateinit var userInfoMateItem3: TextView

    // user 데이터 베이스 모델
    var userInfo = UserInfo()

    // 나이
    private var ageYear: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // 생성
        entranceYear = findViewById(R.id.entranceYear)
        userNickName = findViewById(R.id.userNickName)
        universityName = findViewById(R.id.universityName)
        majorName = findViewById(R.id.majorName)

        // 나이
        userAge = findViewById(R.id.userAge)

        // 재학, 휴학, 졸업
        accountInSchool = findViewById(R.id.accountInSchool)
        accountOffFromSchool = findViewById(R.id.accountOffFromSchool)
        // 배울래 아이템
        userInfoLearnItem1 = findViewById(R.id.userInfoLearnItem1)
        userInfoLearnItem2 = findViewById(R.id.userInfoLearnItem2)
        userInfoLearnItem3 = findViewById(R.id.userInfoLearnItem3)

        // 알려줄래 아이템
        userInfoTeachItem1 = findViewById(R.id.userInfoTeachItem1)
        userInfoTeachItem2 = findViewById(R.id.userInfoTeachItem2)
        userInfoTeachItem3 = findViewById(R.id.userInfoTeachItem3)

        // 함께할래 아이템
        userInfoMateItem1 = findViewById(R.id.userInfoMateItem1)
        userInfoMateItem2 = findViewById(R.id.userInfoMateItem2)
        userInfoMateItem3 = findViewById(R.id.userInfoMateItem3)

        // 성별
        userMale = findViewById(R.id.userMale)
        userFemale = findViewById(R.id.userFemale)

    }

    // 배울래 선택
    private fun LearnSelection(){
        learnSelectionArrayList = getStringArrayPref("learnSelection", "learnSelections")
        if (learnSelectionArrayList.size == 1){
            userInfoLearnItem1.text = learnSelectionArrayList.get(0)

            userInfoLearnItem1.isVisible = true
            userInfoLearnItem2.isInvisible = true
            userInfoLearnItem3.isInvisible = true
        }else if (learnSelectionArrayList.size == 2){
            userInfoLearnItem1.text = learnSelectionArrayList.get(0)
            userInfoLearnItem2.text = learnSelectionArrayList.get(1)

            userInfoLearnItem1.isVisible = true
            userInfoLearnItem2.isVisible = true
            userInfoLearnItem3.isInvisible = true
        }else if(learnSelectionArrayList.size == 3){
            userInfoLearnItem1.text = learnSelectionArrayList.get(0)
            userInfoLearnItem2.text = learnSelectionArrayList.get(1)
            userInfoLearnItem3.text = learnSelectionArrayList.get(2)

            userInfoLearnItem1.isVisible = true
            userInfoLearnItem2.isVisible = true
            userInfoLearnItem3.isVisible = true
        }else{
            learnSelectionArrayList.clear()
            userInfoLearnItem1.isInvisible = true
            userInfoLearnItem2.isInvisible = true
            userInfoLearnItem3.isInvisible = true
        }
    }

    // 알려줄래 선택
    private fun TeachSelection(){
        teachSelectionArrayList = getStringArrayPref("teachSelection", "teachSelections")
        if (teachSelectionArrayList.size == 1){
            userInfoTeachItem1.text = teachSelectionArrayList.get(0)

            userInfoTeachItem1.isVisible = true
            userInfoTeachItem2.isInvisible = true
            userInfoTeachItem3.isInvisible = true
        }else if (teachSelectionArrayList.size == 2){
            userInfoTeachItem1.text = teachSelectionArrayList.get(0)
            userInfoTeachItem2.text = teachSelectionArrayList.get(1)

            userInfoTeachItem1.isVisible = true
            userInfoTeachItem2.isVisible = true
            userInfoTeachItem3.isInvisible = true
        }else if(teachSelectionArrayList.size == 3){
            userInfoTeachItem1.text = teachSelectionArrayList.get(0)
            userInfoTeachItem2.text = teachSelectionArrayList.get(1)
            userInfoTeachItem3.text = teachSelectionArrayList.get(2)

            userInfoTeachItem1.isVisible = true
            userInfoTeachItem2.isVisible = true
            userInfoTeachItem3.isVisible = true
        }else{
            teachSelectionArrayList.clear()
            userInfoTeachItem1.isInvisible = true
            userInfoTeachItem2.isInvisible = true
            userInfoTeachItem3.isInvisible = true
        }
    }

    // 함께할래 선택
    private fun MateSelection(){
        mateSelectionArrayList = getStringArrayPref("mateSelection", "mateSelections")
        if (mateSelectionArrayList.size == 1){
            userInfoMateItem1.text = mateSelectionArrayList.get(0)

            userInfoMateItem1.isVisible = true
            userInfoMateItem2.isInvisible = true
            userInfoMateItem3.isInvisible = true
        }else if (mateSelectionArrayList.size == 2){
            userInfoMateItem1.text = mateSelectionArrayList.get(0)
            userInfoMateItem2.text = mateSelectionArrayList.get(1)

            userInfoMateItem1.isVisible = true
            userInfoMateItem2.isVisible = true
            userInfoMateItem3.isInvisible = true
        }else if(mateSelectionArrayList.size == 3){
            userInfoMateItem1.text = mateSelectionArrayList.get(0)
            userInfoMateItem2.text = mateSelectionArrayList.get(1)
            userInfoMateItem3.text = mateSelectionArrayList.get(2)

            userInfoMateItem1.isVisible = true
            userInfoMateItem2.isVisible = true
            userInfoMateItem3.isVisible = true
        }else{
            mateSelectionArrayList.clear()
            userInfoMateItem1.isInvisible = true
            userInfoMateItem2.isInvisible = true
            userInfoMateItem3.isInvisible = true
        }
    }

    // 성별 (남자-3 / 여자-4)
    fun userMale(v:View){
        gender = 3
        userMale.setImageResource(R.drawable.menred)
        userFemale.setImageResource(R.drawable.womensilver)
    }
    fun userFemale(v:View){
        gender = 4
        userMale.setImageResource(R.drawable.mensilver)
        userFemale.setImageResource(R.drawable.womenred)
    }

    // 배울래, 알려줄래, 함께할래 저장한 값 불러오기
    private fun getStringArrayPref(name: String ,key: String): ArrayList<String>  {
        val prefs = getSharedPreferences(name , MODE_PRIVATE)
        val json = prefs.getString(key, null)
        val gson = Gson()

        if (json != null){
            val learnData: ArrayList<String> = gson?.fromJson(json,
                object : TypeToken<ArrayList<String?>>() {}.type
            )
            return learnData
        }else{
            // 빈 값 리턴
            return selectionArrayList
        }
    }

    // 배울래 선택 이동
    fun learnSelection(v: View){ startActivity(Intent(this, LearnSelectionActivity::class.java)) }

    // 알려줄래 선택 이동
    fun teachSelection(v: View){ startActivity(Intent(this, TeachSelectionActivity::class.java)) }

    // 함께할래 선택 이동
    fun mateSelection(v: View){ startActivity(Intent(this, MateSelectionActivity::class.java)) }

    // 제출하기 버튼
    fun userInfoCheck(v: View) {
        // 이메일 불러오기
        val prefs: SharedPreferences = getSharedPreferences("Email", MODE_PRIVATE)

        if (userNickName?.text.toString().length < 2){
            toastShow("닉네임은 2자 이상으로 해주세요. :)")
        }else if (gender == null ){
            toastShow("성별을 선택해주세요 :)")
        }else if (learnSelectionArrayList.size < 1 ){
            toastShow("배울래를 하나 이상 선택해주세요 :)")
        }else if (teachSelectionArrayList.size < 1 ){
            toastShow("알려줄래를 하나 이상 선택해주세요 :)")
        }else if (mateSelectionArrayList.size < 1 ){
            toastShow("같이할래를 하나 이상 선택해주세요 :)")
        }else if (entranceYear?.text.toString().contains("입학")){
            toastShow("입학년도를 입력해주세요 :)")
        }else if (State == null){
            toastShow("재학 or 휴학을 선택해주세요 :)")
        }else if (filePath == null) {
            toastShow("학생증을 첨부해주세요 :)")
        } else{
            // 학생증
            var imgFileName = user?.uid + "_.png"
            var storageReference= firebaseStore?.reference?.child("StudentCard")?.child(imgFileName)
            storageReference?.putFile(filePath!!)?.addOnSuccessListener {
                toastShow("학생증이 제출되었습니다 :)")
            }
            // 회원 목록 가져오기
            var auth: FirebaseAuth= FirebaseAuth.getInstance()
            var userDB: DatabaseReference = Firebase.database.reference.child(DBKey.USERS)
            val currentUserDB = userDB.child(auth.currentUser?.uid.orEmpty())

            userInfo.userId = auth.currentUser?.uid.orEmpty()

            userInfo.email = prefs.getString("email", "Error !").toString()
            userInfo.userNickName = userNickName?.text.toString()
            userInfo.userAgeYear = if (ageYear == null) null else ageYear
            userInfo.gender = gender


            userInfo.learn_1 = learnSelectionArrayList[0]
            userInfo.learn_2 = if (learnSelectionArrayList.size <= 1) null else learnSelectionArrayList[1]
            userInfo.learn_3 = if (learnSelectionArrayList.size <= 2) null else learnSelectionArrayList[2]

            userInfo.teach_1 = teachSelectionArrayList[0]
            userInfo.teach_2 = if (teachSelectionArrayList.size <= 1) null else teachSelectionArrayList[1]
            userInfo.teach_3 = if (teachSelectionArrayList.size <= 2) null else teachSelectionArrayList[2]

            userInfo.mate_1 = mateSelectionArrayList[0]
            userInfo.mate_2 = if (mateSelectionArrayList.size <= 1) null else mateSelectionArrayList[1]
            userInfo.mate_3 = if (mateSelectionArrayList.size <= 2) null else mateSelectionArrayList[2]

            userInfo.university = if(universityName?.text.toString().isEmpty()) null else universityName?.text.toString()
            userInfo.major = if(majorName?.text.toString().isEmpty()) null else majorName?.text.toString()
            userInfo.state = State
            userInfo.introduction = null

            userInfo.studentCard = 0

            currentUserDB.child(USER_INFORMATION).setValue(userInfo).addOnSuccessListener {
                // firebase store 학생증 올리기
                val userPermissionIntent = Intent(this, UserPermissionActivity::class.java)
                // 액티비티 스택에 전환되는 액티비티만 존재하여 뒤로가기를 눌렀을 때 반응이 없게 됨
                userPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                userPermissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(userPermissionIntent)

                toastShow("회원정보 등록을 완료하였습니다 :)")
            }.addOnFailureListener {
                toastShow("회원정보 등록에 실패하였습니다.")
            }

        }
    }

    // 입학년도 버튼
    fun entranceYearButton(v: View){

        //Log.d("entranceYearButton>> ", "눌림")
        val entranceYearDialog = layoutInflater.inflate(R.layout.dialog_datepicker, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(entranceYearDialog)
            .create()

        val year : NumberPicker = entranceYearDialog.findViewById(R.id.yearpicker_datepicker)               // 년도
        val successEntranceYear = entranceYearDialog.findViewById<Button>(R.id.successEntranceYearButton)
        val closeEntranceYear = entranceYearDialog.findViewById<Button>(R.id.closeEntranceYearButton)

        //  순환 안되게 막기
        year.wrapSelectorWheel = false
        //  최소값 설정
        year.minValue = 2000
        //  최대값 설정
        year.maxValue = 2030
        //  editText 설정 해제
        year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  보여질 값 설정 (string)
        //year.displayedValues = arrayOf("2000년","2000년","2000년","2000년","2000년","2000년","2000년")

        // 취소 버튼 클릭시
        closeEntranceYear.setOnClickListener { alertDialog.dismiss() }

        // 확인 버튼 클릭시
        successEntranceYear.setOnClickListener {
            userInfo.entranceYear = (year.value)
            entranceYear?.text= (year.value).toString() + "년"
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    // 생년월일 버튼
    @SuppressLint("SetTextI18n")
    fun userAge(v: View){
        val ageDialog = layoutInflater.inflate(R.layout.dialog_age, null)
        val ageAlertDialog = AlertDialog.Builder(this)
            .setView(ageDialog)
            .create()

        // 생년월일
        val userYearAge : NumberPicker = ageDialog.findViewById(R.id.yearPicker_datepicker)             // 년
        val userMonthAge : NumberPicker = ageDialog.findViewById(R.id.monthPicker_datepicker)           // 월
        val userDayAge : NumberPicker = ageDialog.findViewById(R.id.dayPicker_datepicker)               // 일

        // 취소, 실패
        val successAgeButton = ageDialog.findViewById<Button>(R.id.successAgeButton)
        val closeAgeButton = ageDialog.findViewById<Button>(R.id.closeAgeButton)

        //  순환 안되게 막기
        userYearAge.wrapSelectorWheel = false
        //  최소값 설정
        userYearAge.minValue = 1990
        userMonthAge.minValue = 1
        userDayAge.minValue = 1
        //  최대값 설정
        userYearAge.maxValue = 2030
        userMonthAge.maxValue = 12
        userDayAge.maxValue = 31

        //  editText 설정 해제
        userYearAge.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        userMonthAge.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        userDayAge.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // 취소 버튼 클릭시
        closeAgeButton.setOnClickListener { ageAlertDialog.dismiss() }

        // 확인 버튼 클릭시
        successAgeButton.setOnClickListener {
            // 나이 추가
            ageYear = userYearAge.value.toString() + getMonth(userMonthAge.value) + getDay(userDayAge.value)
            // Log.d("생년월일", ageYear.toString())
            // Log.d("월일", userMonthAge.value.toString() + userDayAge.value.toString())
            userAge?.text= (userYearAge.value).toString() + "년 " + (userMonthAge.value).toString() + "월 " + (userDayAge.value).toString() + "일"
            ageAlertDialog.dismiss()
        }
        ageAlertDialog.show()
    }
    private fun getMonth(month: Int): String{
        if (month > 0 && month < 10){
            return "0$month"
        }else{
            return month.toString()
        }
    }
    private fun getDay(day: Int): String {
        if (day > 0 && day < 10){
            return "0$day"
        }else{
            return day.toString()
        }
    }

    // Activity에서 학생증 인증 이미지뷰 클릭
    fun uploadStudentCard(v: View) {
        val studentCardDialog = layoutInflater.inflate(R.layout.dialog_studentcard_selection, null)
        val studentCardAlertDialog = AlertDialog.Builder(this)
            .setView(studentCardDialog)
            .create()

        val studentCardCameraDialog : TextView = studentCardDialog.findViewById(R.id.dialog_studentcard_camera)
        val studentCardGalleryDialog : TextView = studentCardDialog.findViewById(R.id.dialog_studentcard_gallery)
        val studentCardCancelDialog : TextView = studentCardDialog.findViewById(R.id.dialog_studentcard_cancel)

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
        val cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
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
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA)
    }

    // 권한 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
        toastShow("카메라 기능 실행")
    }
    private fun launchCamera(){
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
        if(requestCode == REQUEST_STORAGE && resultCode == RESULT_OK ){
            if (data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                studentCardImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }else if (requestCode == REQUEST_CAMERA){
            //카메라로 방금 촬영한 이미지를 미리 만들어 놓은 이미지뷰로 전달 합니다.
            val bitmap = data?.extras?.get("data") as Bitmap
            filePath = getImageUri(this, bitmap)
            studentCardImageView.setImageBitmap(bitmap)
        }
    }
    // 카메라로 찍은 bitmap을 uril로 변환
    private fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        if (inImage != null) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        }
        val path = MediaStore.Images.Media.insertImage(inContext?.getContentResolver(), inImage, "Title" + " - " + Calendar.getInstance().getTime(), null)
        return Uri.parse(path)
    }



    // 재학
    @SuppressLint("ResourceAsColor")
    fun accountInSchool(v:View){
        State = accountInSchool.text.toString()
        accountInSchool.setBackgroundResource(R.drawable.button_mycolor_radius)
        accountInSchool.setTextColor(Color.WHITE)

        accountOffFromSchool.setBackgroundResource(R.drawable.edittext_stroke)
        accountOffFromSchool.setTextColor(R.color.darkGray)
    }
    // 휴학
    @SuppressLint("ResourceAsColor")
    fun accountOffFromSchool(v:View){
        State = accountOffFromSchool.text.toString()
        accountInSchool.setBackgroundResource(R.drawable.edittext_stroke)
        accountInSchool.setTextColor(R.color.darkGray)

        accountOffFromSchool.setBackgroundResource(R.drawable.button_mycolor_radius)
        accountOffFromSchool.setTextColor(Color.WHITE)
    }

    // 다른 액티비티에서 finish 후 돌아왔을 때
    override fun onResume() {
        super.onResume()
        // 배울래
        LearnSelection()
        // 알려줄래
        TeachSelection()
        // 함께할래
        MateSelection()
    }

    fun toastShow(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}



