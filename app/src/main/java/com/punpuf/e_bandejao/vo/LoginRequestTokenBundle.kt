package com.punpuf.e_bandejao.vo

data class LoginRequestTokenBundle(
    val requestTokenToken: String,
    val requestTokenSecret: String,
    val authEndpointUrl: String,
)