package com.punpuf.e_usp.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.punpuf.e_usp.Const

@Entity(tableName = Const.TABLE_SELECTED_RESTAURANT)
data class SelectedRestaurant(

    @PrimaryKey
    @ColumnInfo(name = Const.TABLE_SELECTED_RESTAURANT_ID)
    val id: Int,

    @ColumnInfo(name = Const.TABLE_SELECTED_RESTAURANT_SELECTION_DATE)
    val selectionDate: Long,

)