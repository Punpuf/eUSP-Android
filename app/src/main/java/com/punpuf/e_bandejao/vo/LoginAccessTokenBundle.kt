package com.punpuf.e_bandejao.vo

data class LoginAccessTokenBundle(
    val requestTokenToken: String,
    val requestTokenSecret: String,
    val oauthVerifier: String,
)