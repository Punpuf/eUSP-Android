package com.punpuf.e_bandejao.network

import androidx.lifecycle.LiveData
import com.punpuf.e_bandejao.Const.Companion.NETWORK_URL_CUSTOM_RESTAURANT_LIST
import com.punpuf.e_bandejao.vo.Restaurant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CustomNetworkService {

    @GET("{url}")
    fun fetchRestaurantList(
        @Path(value = "url", encoded = true) url: String = NETWORK_URL_CUSTOM_RESTAURANT_LIST,
        @Query("alt") alt: String = "media",
    ): LiveData<ApiResponse<List<Restaurant>>>

}