package com.atob.daekiri.Fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atob.daekiri.Adapter.MatchedUserAdapter
import com.atob.daekiri.DataClass.ChatRoom
import com.atob.daekiri.R
import com.atob.daekiri.databinding.FragmentOneBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OneFragment : Fragment(R.layout.fragment_one) {

    private var binding: FragmentOneBinding?= null

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = MatchedUserAdapter()
    private val chatRoom = mutableListOf<ChatRoom>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragmentOneBinding = FragmentOneBinding.bind(view)
        binding = fragmentOneBinding
        
        userDB = Firebase.database.reference.child("Users")

        // recyclerView에 레이아웃 매니저 연결
        initMatchedUserRecyclerView()

        // DB에서 매치된 유저 가져오기
        getMatchUsers()
    }


    // recyclerView에 레이아웃 매니저 연결
    private fun initMatchedUserRecyclerView() {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.matchedUserRecyclerView)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
    }

    // DB에서 매치된 유저 가져오기
    private fun getMatchUsers(){

        // 나의 userId의 likeBy에 있는 match 아이디 가져오기
        val matchDB = userDB.child(getCurrentUserID()).child("likedBy").child("match")

        matchDB.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //match에 키가 존재한다면
                if (snapshot.key?.isNotEmpty() == true){
                    // 키를 가져와서 이름을 가져올 함수
                    getUserByKey(snapshot.key.orEmpty())
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 키를 가져와서 리사이클러 뷰에 보여질 이름을 가져올 함수
    private fun getUserByKey(userId: String){
        userDB.child(userId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoom.add(ChatRoom(userId, snapshot.child("name").value.toString()))
                //adapter.submitList(chatRoom)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // UserID 리턴
    private fun getCurrentUserID(): String{

        return auth.currentUser?.uid.orEmpty()
    }

    
}