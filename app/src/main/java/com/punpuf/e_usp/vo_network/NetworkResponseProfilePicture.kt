package com.punpuf.e_usp.vo_network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseProfilePicture(
    @Expose @SerializedName("resultado") val encodedProfilePicture: String?,
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMessage: String?,
)