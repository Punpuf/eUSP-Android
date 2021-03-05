package com.punpuf.e_usp.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_usp.Const
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = Const.TABLE_BOLETO)
data class Boleto(
    @Expose @SerializedName("id")
    @ColumnInfo(name = Const.TABLE_BOLETO_ID)
    @PrimaryKey
    val id: String,

    @Expose @SerializedName("codigoBarras")
    @ColumnInfo(name = Const.TABLE_BOLETO_BARCODE)
    val barcode: String?,

    @Expose @SerializedName("valor")
    @ColumnInfo(name = Const.TABLE_BOLETO_AMOUNT)
    val amount: String?,

    @Expose @SerializedName("vencimento")
    @ColumnInfo(name = Const.TABLE_BOLETO_EXPIRATION_DATE)
    val expirationDate: String?,

)