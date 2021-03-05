package com.punpuf.e_usp.vo_network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.punpuf.e_usp.vo.BookUser

data class NetworkResponseBookUser(
    @Expose @SerializedName("numberOfResultsTotal") val numResults: Int?,
    @Expose @SerializedName("loans") val loanList: List<BookUser>?,
    @Expose @SerializedName("holds") val reservationList: List<BookUser>?,
    @Expose @SerializedName("history") val historyList: List<BookUser>?,
)