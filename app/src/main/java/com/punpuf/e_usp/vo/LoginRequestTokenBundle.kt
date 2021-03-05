package com.punpuf.e_usp.vo

data class LoginRequestTokenBundle(
    val requestTokenToken: String,
    val requestTokenSecret: String,
    val authEndpointUrl: String,
)