package com.punpuf.e_usp.vo_network

import com.punpuf.e_usp.vo.Boleto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseOngoingBoletoList(
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMsg: String?,
    @Expose @SerializedName("boletos") val boletoList: List<Boleto>,
)