package com.punpuf.e_bandejao.network

import androidx.lifecycle.LiveData
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.Const.Companion.NETWORK_BASE_LIBRARY
import com.punpuf.e_bandejao.Const.Companion.NETWORK_ENDPOINT_LIBRARY_MAIN
import com.punpuf.e_bandejao.vo_network.NetworkResponseBookUser
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

}