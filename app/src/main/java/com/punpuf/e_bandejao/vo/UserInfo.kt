package com.punpuf.e_bandejao.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_bandejao.Const.Companion.TABLE_USER_INFO_FIELD_DEPARTMENT_NAME
import com.punpuf.e_bandejao.Const.Companion.TABLE_USER_INFO_FIELD_NAME
import com.punpuf.e_bandejao.Const.Companion.TABLE_USER_INFO_FIELD_NUMBER_USP
import com.punpuf.e_bandejao.Const.Companion.TABLE_USER_INFO_NAME
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = TABLE_USER_INFO_NAME)
data class UserInfo(
    @Expose @SerializedName("loginUsuario")
    @PrimaryKey @ColumnInfo(name = TABLE_USER_INFO_FIELD_NUMBER_USP)
    val numberUSP: String,

    @Expose @SerializedName("nomeUsuario")
    @ColumnInfo(name = TABLE_USER_INFO_FIELD_NAME) val name: String,

    @Expose @SerializedName("nomeUnidade")
    @ColumnInfo(name = TABLE_USER_INFO_FIELD_DEPARTMENT_NAME)
    val departmentName: String,
)