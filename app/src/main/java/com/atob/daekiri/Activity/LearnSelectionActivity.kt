package com.atob.daekiri. Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.atob.daekiri.Adapter.LearnAdapter
import com.atob.daekiri.R
import com.google.firebase.database.*
import com.google.gson.Gson

class LearnSelectionActivity: AppCompatActivity(), AdapterView.OnItemClickListener{

    private var arrayList:ArrayList<String>?=null
    private var gridView: GridView?=null
    private var learnAdapter: LearnAdapter?=null

    // 배우고 싶은 과목 선택
    private var selectionArrayList:ArrayList<String> = ArrayList()

    private lateinit var learnSelection1: LinearLayout
    private lateinit var learnSelection2: LinearLayout
    private lateinit var learnSelection3: LinearLayout

    private lateinit var learnSelectionTextView1: TextView
    private lateinit var learnSelectionTextView2: TextView
    private lateinit var learnSelectionTextView3: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_learn)

        learnSelection1 = findViewById(R.id.learnSelection1)
        learnSelection2 = findViewById(R.id.learnSelection2)
        learnSelection3 = findViewById(R.id.learnSelection3)

        learnSelectionTextView1 = findViewById(R.id.learnSelectionTextView1)
        learnSelectionTextView2 = findViewById(R.id.learnSelectionTextView2)
        learnSelectionTextView3 = findViewById(R.id.learnSelectionTextView3)

        gridView = findViewById(R.id.learnSelectionGridView)

        arrayList= ArrayList()
        // arrayList 데이터 추가
        arrayList= setDataList()
        learnAdapter = LearnAdapter(applicationContext, arrayList!!)
        gridView?.adapter = learnAdapter
        learnAdapter!!.notifyDataSetChanged()

        // 아이템 클릭
        gridView?.onItemClickListener = this


    }

    // learn 데이터 생성
    private fun setDataList(): ArrayList<String>{

        var arrayList:ArrayList<String> = ArrayList()

        // read a message to the database
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = database.getReference("learn_teach")

        //Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (snapshot in snapshot.getChildren()) {
                    var value = snapshot.value.toString()
                    arrayList.add(value)
                    //Log.d("value값 1", value)
                }
                // Log.d("arrayList값 1", arrayList.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //Log.d("arrayList값 2", arrayList.toString())

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


    // 배우고 싶은 과목 아이템 1번째
    fun learnSelection1(v: View){
        selectionArrayList.removeAt(0)
        linearLayoutVisible()
    }

    // 배우고 싶은 과목 아이템 2번째
    fun learnSelection2(v: View){
        selectionArrayList.removeAt(1)
        linearLayoutVisible()
    }

    // 배우고 싶은 과목 아이템 3번째
    fun learnSelection3(v: View){
        selectionArrayList.removeAt(2)
        linearLayoutVisible()
    }

    // 배우고 싶은 과목 컨트롤
    private fun linearLayoutVisible(){
        if (selectionArrayList.size==1){
            learnSelection1.isVisible = true
            learnSelectionTextView1.text = selectionArrayList.get(0)

            learnSelection2.isGone = true
            learnSelection3.isGone = true
        }else if (selectionArrayList.size==2){
            learnSelection2.isVisible = true
            learnSelectionTextView2.text = selectionArrayList.get(1)

            learnSelection3.isGone = true
        }else if (selectionArrayList.size==3){
            learnSelection3.isVisible = true
            learnSelectionTextView3.text = selectionArrayList.get(2)
        }else{
            // selectionArrayList.size < 1 리스트가 비었을 때
            learnSelection1.isGone = true
        }
    }

    // 선택하기
    fun learnSelectionCheck(v: View){
        setStringArrayPref(selectionArrayList)
        finish()
    }

    private fun setStringArrayPref(key: ArrayList<String>) {
        val gson = Gson()
        val json = gson.toJson(key)
        val prefs = getSharedPreferences("learnSelection", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("learnSelections", json)
        editor.apply()
    }


    override fun onRestart() {
        super.onRestart()
        setDataList()
    }
}
