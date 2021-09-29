package com.punpuf.e_usp.vo_network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.punpuf.e_usp.vo.BookOfSearch

data class NetworkResponseBookOfSearch(
    @Expose @SerializedName("pageNumber") val pageNumber: Int?,
    @Expose @SerializedName("numberOfResultsPerPage") val resultsPerPage: Int?,
    @Expose @SerializedName("numberOfResultsTotal") val numResultsTotal: Int?,
    @Expose @SerializedName("publications") val bookOfSearchList: List<BookOfSearch>,
)