package com.punpuf.e_usp.vo_network

data class NetworkRequestBodySubscription(
    val token: String,
    val app: String = "AppEcard",
)