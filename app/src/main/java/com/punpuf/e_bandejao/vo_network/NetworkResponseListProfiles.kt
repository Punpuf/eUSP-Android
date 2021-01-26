package com.punpuf.e_bandejao.vo_network

import com.punpuf.e_bandejao.vo.UserProfile
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseListProfiles(
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMsg: String?,
    @Expose @SerializedName("resultado") val userProfileList: List<UserProfile>,
)