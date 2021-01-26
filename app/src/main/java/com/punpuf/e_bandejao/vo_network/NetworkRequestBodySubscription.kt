package com.punpuf.e_bandejao.vo_network

data class NetworkRequestBodySubscription(
    val token: String,
    val app: String = "AppEcard",
)