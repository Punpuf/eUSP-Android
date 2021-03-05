package com.punpuf.e_usp.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.db.MenuDao
import com.punpuf.e_usp.db.RestaurantDao
import com.punpuf.e_usp.db.SelectedRestaurantDao
import com.punpuf.e_usp.network.ApiResponse
import com.punpuf.e_usp.network.CustomNetworkService
import com.punpuf.e_usp.network.NetworkBoundResource
import com.punpuf.e_usp.network.UspNetworkService
import com.punpuf.e_usp.util.RestaurantMenuResponseConverter
import com.punpuf.e_usp.vo.Resource
import com.punpuf.e_usp.vo.Restaurant
import com.punpuf.e_usp.vo.SelectedRestaurant
import com.punpuf.e_usp.vo.WeeklyMenu
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import timber.log.Timber
import timber.log.Timber.d
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BandejaoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restaurantDao: RestaurantDao,
    private val menuDao: MenuDao,
    private val selectedRestaurantDao: SelectedRestaurantDao,
    private val uspNetworkService: UspNetworkService,
    private val customNetworkService: CustomNetworkService,
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getSelectedRestaurant(): LiveData<SelectedRestaurant?> {
        return selectedRestaurantDao.getSelectedRestaurant()
    }

    suspend fun fetchRestaurantById(restaurantId: Int): LiveData<Resource<Restaurant?>> {
        d("Getting current restaurant")
        return object : NetworkBoundResource<Restaurant?, List<Restaurant>>() {
            override suspend fun loadFromDb(): LiveData<Restaurant?> {
                d("load from db")
                return restaurantDao.getRestaurantById(restaurantId)
            }

            override suspend fun shouldFetch(data: Restaurant?): Boolean {
                d("shouldFetch?: $data")
                if (data == null) return true

                val now = System.currentTimeMillis()
                val lastFetch = sharedPreferences.getLong(Const.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT, now)

                return TimeUnit.MILLISECONDS.toDays(now - lastFetch) > 14
            }

            override suspend fun createCall(): LiveData<ApiResponse<List<Restaurant>>> {
                d("createCall")
                return customNetworkService.fetchRestaurantList()
            }

            override suspend fun saveCallResult(result: List<Restaurant>) {
                d("saveCallResult: $result")
                restaurantDao.insertRestaurantList(result)
                sharedPreferences.edit().putLong(
                    Const.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT,
                    System.currentTimeMillis()
                ).apply()
            }

        }.build()
    }

    suspend fun fetchRestaurantMenu(restaurantId: Int): LiveData<Resource<WeeklyMenu?>> {
        return object : NetworkBoundResource<WeeklyMenu?, String>() {
            override suspend fun loadFromDb(): LiveData<WeeklyMenu?> {
                return menuDao.getMenuByRestaurantId(restaurantId)
            }

            override suspend fun shouldFetch(data: WeeklyMenu?): Boolean {
                if (data == null) return true
                return data.expirationDate ?: 0 < System.currentTimeMillis()
            }

            override suspend fun createCall(): LiveData<ApiResponse<String>> {
                return uspNetworkService.fetchRestaurantMenu(restaurantId = restaurantId.toString())
            }

            override suspend fun saveCallResult(result: String) {
                try {
                    val weeklyMenu = RestaurantMenuResponseConverter.convertResponse(restaurantId, JSONObject(result))
                    Timber.d("weeklyMenu is: $weeklyMenu")

                    menuDao.insertMenu(weeklyMenu)
                } catch (exception: Exception) { Timber.e("fetchRestaurantMenu error: $exception") }
            }

        }.build()
    }

}