package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_usp.Const.Companion.TABLE_SELECTED_RESTAURANT
import com.punpuf.e_usp.Const.Companion.TABLE_SELECTED_RESTAURANT_SELECTION_DATE
import com.punpuf.e_usp.vo.SelectedRestaurant

@Dao
interface SelectedRestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectedRestaurant(selectedRestaurant: SelectedRestaurant)

    @Query("SELECT * FROM $TABLE_SELECTED_RESTAURANT ORDER BY $TABLE_SELECTED_RESTAURANT_SELECTION_DATE DESC LIMIT 1")
    fun getSelectedRestaurant(): LiveData<SelectedRestaurant?>

    @Query("DELETE FROM $TABLE_SELECTED_RESTAURANT")
    suspend fun deleteAll()
}