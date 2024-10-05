package com.dicoding.geotaggingjbg.data.pref

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val uid: Int,
    val expireTime: Long
)