package com.punpuf.e_bandejao.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_FIELD_TOKEN
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_NAME

@Entity (tableName = TABLE_TOKEN_NAME)
data class Token (
    @PrimaryKey val id: Int,
    @ColumnInfo (name = TABLE_TOKEN_FIELD_TOKEN) val token: String?,
)