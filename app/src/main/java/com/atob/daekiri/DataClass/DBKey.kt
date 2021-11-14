package com.atob.daekiri.DataClass

/**
 * Firebase Realtime KEY 값
 */
class DBKey {
    companion object{
        /*Root*/
        const val USERS = "Users"                       // 회원 uid
        /*USERS 안에*/
        const val USER_INFORMATION = "userInformation"  // 회원 정보


        const val LIKED_BY = "likedBy"
        const val LIKE = "like"                         // like
        const val DIS_LIKE="disLike"                    // dislike



        // 카드의 유저아이디와 네임
        const val USER_ID="userId"
        const val NAME="name"

    }
}