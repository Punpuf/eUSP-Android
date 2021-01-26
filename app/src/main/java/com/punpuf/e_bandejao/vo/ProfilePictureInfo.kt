package com.punpuf.e_bandejao.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_bandejao.Const.Companion.TABLE_PROFILE_PIC_FIELD_DOWNLOAD_DATE
import com.punpuf.e_bandejao.Const.Companion.TABLE_PROFILE_PIC_FIELD_LOCATION
import com.punpuf.e_bandejao.Const.Companion.TABLE_PROFILE_PIC_NAME

@Entity(tableName = TABLE_PROFILE_PIC_NAME)
data class ProfilePictureInfo (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = TABLE_PROFILE_PIC_FIELD_LOCATION) val location: String,
    @ColumnInfo(name = TABLE_PROFILE_PIC_FIELD_DOWNLOAD_DATE) val downloadDate: Long, // time of the last fetch of profile picture (value is millis since epoch)
)