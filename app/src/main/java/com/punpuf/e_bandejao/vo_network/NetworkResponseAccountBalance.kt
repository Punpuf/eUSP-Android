package com.punpuf.e_bandejao.vo_network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseAccountBalance(
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMsg: String?,
    @Expose @SerializedName("saldo") val accountBalance: String?,
)