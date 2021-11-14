package com.atob.daekiri.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.atob.daekiri.Adapter.MateAdapter
import com.atob.daekiri.R
import com.google.firebase.database.*
import com.google.gson.Gson

class MateSelectionActivity: AppCompatActivity(), AdapterView.OnItemClickListener{

    private var arrayList:ArrayList<String>?=null
    private var gridView: GridView?=null
    private var mateAdapter: MateAdapter?=null

    // 함께 선택
    private var selectionArrayList:ArrayList<String> = ArrayList()

    private lateinit var mateSelection1: LinearLayout
    private lateinit var mateSelection2: LinearLayout
    private lateinit var mateSelection3: LinearLayout

    private lateinit var mateSelectionTextView1: TextView
    private lateinit var mateSelectionTextView2: TextView
    private lateinit var mateSelectionTextView3: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_mate)

        mateSelection1 = findViewById(R.id.mateSelection1)
        mateSelection2 = findViewById(R.id.mateSelection2)
        mateSelection3 = findViewById(R.id.mateSelection3)

        mateSelectionTextView1 = findViewById(R.id.mateSelectionTextView1)
        mateSelectionTextView2 = findViewById(R.id.mateSelectionTextView2)
        mateSelectionTextView3 = findViewById(R.id.mateSelectionTextView3)

        gridView = findViewById(R.id.mateSelectionGridView)

        arrayList= ArrayList()
        // arrayList 데이터 추가
        arrayList= setDataList()
        mateAdapter = MateAdapter(applicationContext, arrayList!!)
        gridView?.adapter = mateAdapter

        // 아이템 클릭
        gridView?.onItemClickListener = this


    }

    // learn 데이터 생성
    private fun setDataList(): ArrayList<String>{

        var arrayList:ArrayList<String> = ArrayList()

        // read a message to the database
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("mate")

        //Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot in snapshot.getChildren()) {
                    var value = snapshot.value.toString()
                    arrayList.add(value)
                    Log.d("value값 3", value)
                }
                Log.d("arrayList값 3", arrayList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return arrayList
    }

    // 아이템 클릭
    @SuppressLint("LongLogTag")
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var listItem: String = arrayList!!.get(position)

        if (selectionArrayList.size  >=  3 ){
            Toast.makeText(applicationContext, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
        }else if (selectionArrayList.contains(listItem)){
            Toast.makeText(applicationContext, "이미 선택한 항목입니다.", Toast.LENGTH_LONG).show()
            return
        }else{
            listItem.let { selectionArrayList.add(it)
            linearLayoutVisible()
            }
        }
    }


    // 함께할래 아이템 1번째
    fun mateSelection1(v: View){
        selectionArrayList.removeAt(0)
        linearLayoutVisible()
    }
    // 함께할래 아이템 2번째
    fun mateSelection2(v: View){
        selectionArrayList.removeAt(1)
        linearLayoutVisible()
    }
    // 함께할래 아이템 3번째
    fun mateSelection3(v: View){
        selectionArrayList.removeAt(2)
        linearLayoutVisible()
    }

    // 함께할래 컨트롤
    private fun linearLayoutVisible(){
        if (selectionArrayList.size==1){
            mateSelection1.isVisible = true
            mateSelectionTextView1.text = selectionArrayList.get(0)

            mateSelection2.isGone = true
            mateSelection3.isGone = true
        }else if (selectionArrayList.size==2){
            mateSelection2.isVisible = true
            mateSelectionTextView2.text = selectionArrayList.get(1)

            mateSelection3.isGone = true
        }else if (selectionArrayList.size==3){
            mateSelection3.isVisible = true
            mateSelectionTextView3.text = selectionArrayList.get(2)
        }else{
            // selectionArrayList.size < 1 리스트가 비었을 때
            mateSelection1.isGone = true
        }
    }

    // 선택하기
    fun mateSelectionCheck(v: View){
        setStringArrayPref(selectionArrayList)
        finish()
    }

    private fun setStringArrayPref(key: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(key)
        val prefs = getSharedPreferences("mateSelection", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("mateSelections", json)
        editor.apply()
    }

}
