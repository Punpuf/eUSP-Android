package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.punpuf.e_usp.Const.Companion.TABLE_RESTAURANT
import com.punpuf.e_usp.Const.Companion.TABLE_RESTAURANT_FIELD_CAMPUS_NAME
import com.punpuf.e_usp.Const.Companion.TABLE_RESTAURANT_FIELD_LAST_USED
import com.punpuf.e_usp.Const.Companion.TABLE_RESTAURANT_FIELD_NAME
import com.punpuf.e_usp.vo.Restaurant

@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurantList(restaurantList: List<Restaurant>)

    @Query("SELECT * FROM $TABLE_RESTAURANT WHERE id = :id LIMIT 1")
    fun getRestaurantById(id: Int): LiveData<Restaurant?>

    @Query("SELECT * FROM $TABLE_RESTAURANT ORDER BY $TABLE_RESTAURANT_FIELD_LAST_USED DESC, " +
            "$TABLE_RESTAURANT_FIELD_CAMPUS_NAME ASC, $TABLE_RESTAURANT_FIELD_NAME ASC")
    fun getRestaurantList(): LiveData<List<Restaurant>>

    @Query("SELECT * FROM $TABLE_RESTAURANT ORDER BY $TABLE_RESTAURANT_FIELD_LAST_USED")
    fun getLatestRestaurant(): LiveData<Restaurant?>

    @Delete
    suspend fun deleteRestaurant(restaurant: Restaurant)

    @Query("DELETE FROM $TABLE_RESTAURANT")
    suspend fun deleteAllRestaurants()

}