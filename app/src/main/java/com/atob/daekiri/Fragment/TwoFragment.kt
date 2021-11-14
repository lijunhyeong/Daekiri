package com.atob.daekiri.Fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.atob.daekiri.Adapter.CardItemAdapter
import com.atob.daekiri.DataClass.CardItem
import com.atob.daekiri.DataClass.DBKey.Companion.DIS_LIKE
import com.atob.daekiri.DataClass.DBKey.Companion.LIKE
import com.atob.daekiri.DataClass.DBKey.Companion.LIKED_BY
import com.atob.daekiri.DataClass.DBKey.Companion.USERS
import com.atob.daekiri.DataClass.DBKey.Companion.USER_ID
import com.atob.daekiri.DataClass.DBKey.Companion.USER_INFORMATION
import com.atob.daekiri.R
import com.atob.daekiri.databinding.FragmentTwoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import java.util.*

class TwoFragment : Fragment(R.layout.fragment_two), CardStackListener {

    private var binding: FragmentTwoBinding?= null

    // 회원 목록
    private var auth: FirebaseAuth= FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    // 카드스택 어댑터
    private val adapter = CardItemAdapter()
    // 카드 아이템을 저장하는 리스트
    private val cardItems = mutableListOf<CardItem>()

    // 카드뷰 매니저
    private val manager by lazy {
        CardStackLayoutManager(context, this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var fragmentTwoBinding = FragmentTwoBinding.bind(view)
        binding = fragmentTwoBinding

        // 회원 목록 가져오기
        userDB = Firebase.database.reference.child(USERS)
        val currentUserDB = userDB.child(getCurrentUserID())

        // currentUserDB 최초 한 번만 부름름
       currentUserDB.child(USER_INFORMATION).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // snapshot 우리 user 정보
                if (snapshot.child("userNickName").value == null){
                    return
                }
                // todo 유저정보 갱싱해라
                // 한번도 선택하지 않은 유저정보를 가져옴
                getUnSelectedUsers()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // 카드스택뷰 초기화
        initCardStackView()

        // 카드뷰 크기 구하기
        cardStackViewSize()

        /*// todo 유저정보 갱싱해라, 가져와라
        // 한번도 선택하지 않은 유저정보를 가져옴
        getUnSelectedUsers()*/

    }

    // 카드뷰 크기 구하기
    @RequiresApi(Build.VERSION_CODES.R)
    private fun cardStackViewSize(){

        // CardStackView 선언
        val stackView = view?.findViewById<CardStackView>(R.id.cardStackView)

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        try {
            requireContext().display?.getRealMetrics(displayMetrics)
        } catch (e: NoSuchMethodError) {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }

        val displayPixelWidth =displayMetrics.widthPixels
        val displayPixelHeight = (displayPixelWidth / 3) * 2

        stackView!!.layoutParams.width = displayPixelWidth
        stackView.layoutParams.height = displayPixelHeight

    }


    // 카드스택뷰 초기화
    private fun initCardStackView(){
        // CardStackView 선언
        val stackView = view?.findViewById<CardStackView>(R.id.cardStackView)

        // null 체크 아니면 . 사이에 ?
        stackView?.layoutManager = CardStackLayoutManager(context, this)
        stackView?.adapter = adapter
    }

    // 한번도 선택하지 않은 유저정보를 가져옴
    private fun getUnSelectedUsers(){

        // userDB에서 발생하는 모든 변경 사항이 이 이벤트로 들어옴
        userDB.addChildEventListener(object : ChildEventListener {

            // 선택되지 않은 유저들만 봄
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 유저아이디 값이 나랑 같지 않아야 한다 && 나의 라이크 또는 디스라이크를 상대 정보에 저장함 (있지 않아야함)
                // && 상대방의 likeBy에 like 또는  dislike 값에 내가 있는지 확인 (내가 없어야 함)
                if (snapshot.child(USER_INFORMATION).child(USER_ID).value != getCurrentUserID()
                    || snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserID()).not()
                    || snapshot.child(LIKED_BY).child(DIS_LIKE).hasChild(getCurrentUserID()).not()){

                    // item card에 들어 갈 값 가져옴
                    val userId = snapshot.child(USER_INFORMATION).child("userId").value.toString()
                    val profile = snapshot.child(USER_INFORMATION).child("profile").value.toString()
                    val age = snapshot.child(USER_INFORMATION).child("userAgeYear").value.toString()
                    val major = snapshot.child(USER_INFORMATION).child("major").value.toString()
                    val name = snapshot.child(USER_INFORMATION).child("userNickName").value.toString()

                    val teachItem1 = snapshot.child(USER_INFORMATION).child("teach_1").value.toString()
                    val teachItem2 = snapshot.child(USER_INFORMATION).child("teach_2").value.toString()
                    val teachItem3 = snapshot.child(USER_INFORMATION).child("teach_3").value.toString()

                    val learnItem1 = snapshot.child(USER_INFORMATION).child("learn_1").value.toString()
                    val learnItem2 = snapshot.child(USER_INFORMATION).child("learn_2").value.toString()
                    val learnItem3 = snapshot.child(USER_INFORMATION).child("learn_3").value.toString()

                    val mateItem1 = snapshot.child(USER_INFORMATION).child("mate_1").value.toString()
                    val mateItem2 = snapshot.child(USER_INFORMATION).child("mate_2").value.toString()
                    val mateItem3 = snapshot.child(USER_INFORMATION).child("mate_3").value.toString()

                    // 생성된 아이템리스트에 넣어줌
                    cardItems.add(CardItem(userId, profile, age, major, name, teachItem1, teachItem2, teachItem3, learnItem1, learnItem2, learnItem3, mateItem1, mateItem2, mateItem3))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }

            // 이름이 바꼈을 떄, 다른 유저가 다른유저를 라이크 했을 때, 새로운 유저가 등록해서 들어올 경우
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 만약 이름이 바뀌었을 경우. 가져온 유저아이디가 스냅샷에 나와있는 키와 동일하다면, 변경된 유저를 찾음
                cardItems.find { it.userId == snapshot.key }?.let {
                    // 유저 네임에 스탭샷에 가져옴 값을 넣어줌
                    it.name = snapshot.child("userNickName").value.toString()
                }

                // 수정이 되었다면 카드 아이템에 갱신을 해줌
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }



    // UserID 리턴
    private fun getCurrentUserID(): String{

        return auth.currentUser?.uid.orEmpty()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    // 라이크 일 때
    private fun like(){
        var card = cardItems[manager.topPosition]
        // 데이터를 지워버림 15분
        cardItems.removeFirst()

        // 상대 아이디, 선택된 상대
        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        // 나의 likeby에 상대방이 나를 like한 적이 있다면 매칭
        saveMatchIfOtherUserLikedMe(card.userId)

        // TODO 저 사람이 나를 라이크 했다는 것을 알기 위해 된 시점을 봐야한다.
        Toast.makeText(context, "${card.name}님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()
    }
    private fun saveMatchIfOtherUserLikedMe(otherUserId: String){
        // true면 상대방이 나를 좋아한 적이 있는 거
        val otherUserDB = userDB.child(getCurrentUserID()).child(LIKED_BY).child(LIKE).child(otherUserId)

        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true){
                    userDB.child(getCurrentUserID())
                        .child(LIKED_BY)
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    // 상대방의 db에도 나랑 매치가 되었다는 것을 저장
                    userDB.child(otherUserId)
                        .child(LIKED_BY)
                        .child("match")
                        .child(getCurrentUserID())
                        .setValue(true)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    // 디스 라이크 일 때
    private fun disLike(){
        val card = cardItems[manager.topPosition-1]
        // 데이터를 지워버림 15분
        cardItems.removeFirst()

        // 상대 아이디, 선택된 상대
        userDB.child(card.userId)
            .child(LIKED_BY)
            .child(DIS_LIKE)
            .child(getCurrentUserID())
            .setValue(true)

        Toast.makeText(context, "${card.name}님을 disLike 하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    // 카드 스와이프 방향
    override fun onCardSwiped(direction: Direction?) {
        when(direction){
            Direction.Right -> like()
            Direction.Left -> disLike()
            else->{

            }
        }

    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) { }





}