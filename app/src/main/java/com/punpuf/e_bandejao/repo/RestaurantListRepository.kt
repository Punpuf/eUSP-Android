package com.punpuf.e_bandejao.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.db.MenuDao
import com.punpuf.e_bandejao.db.RestaurantDao
import com.punpuf.e_bandejao.db.SelectedRestaurantDao
import com.punpuf.e_bandejao.network.ApiResponse
import com.punpuf.e_bandejao.network.CustomNetworkService
import com.punpuf.e_bandejao.network.NetworkBoundResource
import com.punpuf.e_bandejao.network.UspNetworkService
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.Restaurant
import com.punpuf.e_bandejao.vo.SelectedRestaurant
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber.d
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RestaurantListRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restaurantDao: RestaurantDao,
    private val selectedRestaurantDao: SelectedRestaurantDao,
    private val customNetworkService: CustomNetworkService,
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    suspend fun getRestaurantList(): LiveData<Resource<List<Restaurant>>> {
        return object : NetworkBoundResource<List<Restaurant>, List<Restaurant>>() {

            override suspend fun loadFromDb(): LiveData<List<Restaurant>> {
                return restaurantDao.getRestaurantList()
            }

            override suspend fun shouldFetch(data: List<Restaurant>?): Boolean {
                if (data.isNullOrEmpty()) return true

                val now = System.currentTimeMillis()
                val lastFetch = sharedPreferences.getLong(Const.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT_LIST, now)

                return TimeUnit.MILLISECONDS.toDays(now - lastFetch) > 7
            }

            override suspend fun createCall(): LiveData<ApiResponse<List<Restaurant>>> {
                return customNetworkService.fetchRestaurantList()
            }

            override suspend fun saveCallResult(result: List<Restaurant>) {
                restaurantDao.insertRestaurantList(result)
                sharedPreferences.edit().putLong(
                    Const.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT_LIST,
                    System.currentTimeMillis()).apply()
            }

        }.build()
    }

    suspend fun setSelectedRestaurant(restaurantId: Int) {
        selectedRestaurantDao.insertSelectedRestaurant(
            SelectedRestaurant(restaurantId, System.currentTimeMillis()))
        d("I am back from setting setSelectedRestaurant")
    }

}