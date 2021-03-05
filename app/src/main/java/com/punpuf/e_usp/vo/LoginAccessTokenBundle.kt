package com.punpuf.e_usp.vo

data class LoginAccessTokenBundle(
    val requestTokenToken: String,
    val requestTokenSecret: String,
    val oauthVerifier: String,
)