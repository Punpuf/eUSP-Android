package com.punpuf.e_usp.vo_network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NetworkResponseGenerateBoleto(
    @Expose @SerializedName("id") val id: String?,
    @Expose @SerializedName("valor") val amount: String?,
    @Expose @SerializedName("codigoBarras") val barcode: String?,
    @Expose @SerializedName("vencimento") val expirationDate: String?,
    @Expose @SerializedName("email") val email: String?,
    @Expose @SerializedName("erro") val hasError: Boolean?,
    @Expose @SerializedName("mensagemErro") val errorMsg: String?,
)