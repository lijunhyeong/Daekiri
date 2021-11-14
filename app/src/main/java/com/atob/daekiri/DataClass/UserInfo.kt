package com.atob.daekiri.DataClass

data class UserInfo(
    var userId: String?=null,
    var email: String? = null,              // 이메일
    var userNickName: String? = null,       // 닉네임
    var userAgeYear:String?=null,              // 생년월일
    var gender: Int? = null,                // 성별 3-남자 / 4-여자

    var learn_1:String ?= null,             // 배울래 1
    var learn_2:String ?= null,             // 배울래 2
    var learn_3:String ?= null,             // 배울래 3

    var teach_1: String?=null,              // 알려줄래1
    var teach_2: String?=null,              // 알려줄래2
    var teach_3: String?=null,              // 알려줄래3

    var mate_1:String?=null,                // 같이할래1
    var mate_2:String?=null,                // 같이할래2
    var mate_3:String?=null,                // 같이할래3

    var university: String? = null,         // 대학명
    var major: String? = null,              // 학과명
    var entranceYear:Int?=null,          // 입학년도
    var state: String?=null,                // 학기 상태(휴학 or 재학)

    var introduction: String? = null,
    var studentCard: Int? = null,
)

