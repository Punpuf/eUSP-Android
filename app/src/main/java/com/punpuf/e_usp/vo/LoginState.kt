package com.punpuf.e_usp.vo

enum class LoginState {
    START,
    FETCHING_AUTH_URL,
    AWAITING_AUTH_VERIFIER,
    FETCHING_TOKEN,
    COMPLETE,
    WAITING_TO_RETRY
}
