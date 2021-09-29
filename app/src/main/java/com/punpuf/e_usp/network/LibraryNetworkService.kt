package com.punpuf.e_usp.network

import androidx.lifecycle.LiveData
import com.punpuf.e_usp.Const.Companion.NETWORK_BASE_LIBRARY
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_LIBRARY_MAIN
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_LIBRARY_SEARCH
import com.punpuf.e_usp.vo_network.NetworkResponseBookOfSearch
import com.punpuf.e_usp.vo_network.NetworkResponseBookUser
import retrofit2.http.*

interface LibraryNetworkService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    fun fetchLibraryLoans(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_MAIN,
        @Field("listLoans") listLoans: String = "1",
        @Field("loginVersion") loginVersion: String = "1",
        @Field("token") token: String,
    ): LiveData<ApiResponse<NetworkResponseBookUser>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    fun fetchLibraryReservations(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_MAIN,
        @Field("listHolds") listLoans: String = "1",
        @Field("loginVersion") loginVersion: String = "1",
        @Field("token") token: String,
    ): LiveData<ApiResponse<NetworkResponseBookUser>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    fun fetchLibraryHistory(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_MAIN,
        @Field("loanHistory") listLoans: String = "1",
        @Field("loginVersion") loginVersion: String = "1",
        @Field("token") token: String,
    ): LiveData<ApiResponse<NetworkResponseBookUser>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    fun requestLibraryLoanRenewal(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_MAIN,
        @Field("token") token: String,
        @Field("sysno") sysNo: String,
        @Field("seq") seq: String,
        @Field("renewLoan") listLoans: String = "1",
        @Field("loginVersion") loginVersion: String = "1",
    ): LiveData<ApiResponse<NetworkResponseBookUser>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    fun requestLibraryReservationCancellation(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_MAIN,
        @Field("token") token: String,
        @Field("sysno") sysNo: String,
        @Field("seq") seq: String,
        @Field("holdseq") holdSeq: String,
        @Field("cancelHold") listLoans: String = "1",
        @Field("loginVersion") loginVersion: String = "1",
    ): LiveData<ApiResponse<NetworkResponseBookUser>>


    
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("{url}")
    suspend fun requestLibraryBookOfSearchList(
        @Path(value = "url", encoded = true) url: String = NETWORK_BASE_LIBRARY + NETWORK_ENDPOINT_LIBRARY_SEARCH,
        @Field("page") page: Int,
        @Field("search") query : String,
        //@Field("resourceType") materialType: String,
        //@Field("options") searchBy: String,
        //@Field("library") library: String,
    ): NetworkResponseBookOfSearch
}