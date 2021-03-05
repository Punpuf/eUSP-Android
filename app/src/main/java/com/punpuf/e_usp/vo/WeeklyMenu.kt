package com.punpuf.e_usp.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.punpuf.e_usp.Const.Companion.TABLE_MENU
import com.punpuf.e_usp.Const.Companion.TABLE_MENU_FIELD_EXPIRATION_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_MENU_FIELD_MEALS
import com.punpuf.e_usp.Const.Companion.TABLE_MENU_FIELD_OBSERVATION
import com.punpuf.e_usp.Const.Companion.TABLE_MENU_FIELD_RESTAURANT_ID

@Entity(tableName = TABLE_MENU)
data class WeeklyMenu(

    @PrimaryKey
    @ColumnInfo(name = TABLE_MENU_FIELD_RESTAURANT_ID)
    val restaurantId: Int,

    @SerializedName("observation")
    @ColumnInfo(name = TABLE_MENU_FIELD_OBSERVATION)
    val observation: String?,

    @SerializedName("meals")
    @ColumnInfo(name = TABLE_MENU_FIELD_MEALS)
    val dailyMenus: List<DailyMenu>,

    @ColumnInfo(name = TABLE_MENU_FIELD_EXPIRATION_DATE)
    val expirationDate: Long?

)