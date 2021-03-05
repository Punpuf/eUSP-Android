package com.punpuf.e_usp.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_usp.Const
import com.google.gson.annotations.SerializedName

@Entity(tableName = Const.TABLE_RESTAURANT)
data class Restaurant(

    @SerializedName("id")
    @PrimaryKey
    val id: Int,

    @SerializedName("name")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_NAME)
    val name: String?,

    @SerializedName("campusName")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_CAMPUS_NAME)
    val campusName: String?,

    @SerializedName("thumbnailUrl")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_THUMBNAIL_URL)
    val thumbnailUrl: String?,

    @SerializedName("address")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_ADDRESS)
    val address: String?,

    @SerializedName("latitude")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_LATITUDE)
    val latitude: String?,

    @SerializedName("longitude")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_LONGITUDE)
    val longitude: String?,

    @SerializedName("phoneNumber")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_PHONE_NUMBER)
    val phoneNumber: String?,

    @SerializedName("workingHours_weekday")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_WORKING_HOURS_WEEKDAYS)
    val workingHours_weekday: String?,

    @SerializedName("workingHours_saturday")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_WORKING_HOURS_SATURDAY)
    val workingHours_saturday: String?,

    @SerializedName("workingHours_sunday")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_WORKING_HOURS_SUNDAY)
    val workingHours_sunday: String?,

    @SerializedName("cashierInfo")
    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_CASHIER_INFO)
    val cashierInfo: String?,

    @ColumnInfo(name = Const.TABLE_RESTAURANT_FIELD_LAST_USED)
    val lastUsed: Long?,

)