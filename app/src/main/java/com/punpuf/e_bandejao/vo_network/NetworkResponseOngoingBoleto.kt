package com.punpuf.e_bandejao.vo_network

import com.punpuf.e_bandejao.vo.Boleto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseOngoingBoleto(
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMsg: String?,
    @Expose @SerializedName("boletos") val ongoingBoletoList: List<Boleto>,
)