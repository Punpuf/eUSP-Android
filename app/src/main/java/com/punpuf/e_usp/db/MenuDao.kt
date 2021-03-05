package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.Const.Companion.TABLE_MENU_FIELD_RESTAURANT_ID
import com.punpuf.e_usp.vo.WeeklyMenu

@Dao
interface MenuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenu(menu: WeeklyMenu)

    @Query("SELECT * FROM ${Const.TABLE_MENU} WHERE $TABLE_MENU_FIELD_RESTAURANT_ID = :restaurantId LIMIT 1")
    fun getMenuByRestaurantId(restaurantId: Int): LiveData<WeeklyMenu?>

    @Query("DELETE FROM ${Const.TABLE_MENU}")
    suspend fun deleteAllMenus()
}