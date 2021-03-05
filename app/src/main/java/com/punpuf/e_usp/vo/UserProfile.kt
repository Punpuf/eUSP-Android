package com.punpuf.e_usp.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_CARD_EXPIRATION_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_USER_DEPARTMENT
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN_EXPIRATION_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_USER_GROUP
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_USER_TYPE
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_FIELD_USER_TYPE_CODE
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_NAME
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity (tableName = TABLE_USER_PROFILE_NAME)
data class UserProfile (
    @PrimaryKey val id: Int?,

    @Expose @SerializedName("grpctousp")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_USER_TYPE)
    val userType: String?,

    @Expose @SerializedName("codctgctousp")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_USER_TYPE_CODE)
    val userTypeCode: String?, //perhaps student vs other category identifier

    @Expose @SerializedName("dtlgrp")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_USER_GROUP)
    val userGroup: String?,

    @Expose @SerializedName("nomclgundcto")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_USER_DEPARTMENT)
    val userDepartment: String?,

    @Expose @SerializedName("token")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN)
    val qrCodeToken: String?,

    @Expose @SerializedName("validade")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_QR_CODE_TOKEN_EXPIRATION_DATE)
    val qrCodeTokenExpiration: String?,

    @Expose @SerializedName("dtavalcto")
    @ColumnInfo (name = TABLE_USER_PROFILE_FIELD_CARD_EXPIRATION_DATE)
    val cardExpirationDate: String?,
)