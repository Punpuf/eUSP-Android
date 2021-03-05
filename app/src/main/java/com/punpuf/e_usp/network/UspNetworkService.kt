package com.punpuf.e_usp.network

import androidx.lifecycle.LiveData
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_CANCEL_BOLETO
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_CARD_PICTURE
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_FETCH_ACCOUNT_BALANCE
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_FETCH_ONGOING_BOLETO
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_FETCH_RESTAURANT_LIST
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_FETCH_RESTAURANT_MENU
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_GENERATE_BOLETO
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_LIST_PROFILES
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_LOGOUT
import com.punpuf.e_usp.Const.Companion.NETWORK_ENDPOINT_REGISTER
import com.punpuf.e_usp.vo_network.*
import retrofit2.Response
import retrofit2.http.*

interface UspNetworkService {

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_LIST_PROFILES)
    fun fetchUserProfileListData(@Body body: NetworkRequestBodyToken): LiveData<ApiResponse<NetworkResponseListProfiles>>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_LIST_PROFILES)
    suspend fun fetchUserProfileList(@Body body: NetworkRequestBodyToken): Response<NetworkResponseListProfiles>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_REGISTER)
    suspend fun subscribeUser(@Body body: NetworkRequestBodySubscription): Response<String>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_LOGOUT)
    suspend fun unsubscribeUser(@Body body: NetworkRequestBodySubscription): Response<String>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_CARD_PICTURE)
    fun fetchUserProfilePictureData(@Body body: NetworkRequestBodyToken): LiveData<ApiResponse<NetworkResponseProfilePicture>>



    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_FETCH_ACCOUNT_BALANCE)
    fun fetchAccountBalance(@Body body: NetworkRequestBodyToken): LiveData<ApiResponse<NetworkResponseAccountBalance>>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_GENERATE_BOLETO)
    fun fetchGeneratedBoleto(@Body body: NetworkRequestGenerateBoleto): LiveData<ApiResponse<NetworkResponseGenerateBoleto>>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_FETCH_ONGOING_BOLETO)
    fun fetchOngoingBoletoList(@Body body: NetworkRequestBodyToken): LiveData<ApiResponse<NetworkResponseOngoingBoletoList>>

    @Headers("Content-Type: application/json;charset=utf-8")
    @POST(NETWORK_ENDPOINT_CANCEL_BOLETO)
    fun cancelBoleto(@Body body: NetworkRequestDeleteBoleto): LiveData<ApiResponse<String>>



    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST(NETWORK_ENDPOINT_FETCH_RESTAURANT_LIST)
    suspend fun fetchRestaurantList(
        @Field("hash") hash: String = "596df9effde6f877717b4e81fdb2ca9f"
    ): Response<String>

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @FormUrlEncoded
    @POST("$NETWORK_ENDPOINT_FETCH_RESTAURANT_MENU/{restaurantId}")
    fun fetchRestaurantMenu(
        @Field("hash") hash: String = "596df9effde6f877717b4e81fdb2ca9f",
        @Path("restaurantId") restaurantId: String
    ): LiveData<ApiResponse<String>>


}