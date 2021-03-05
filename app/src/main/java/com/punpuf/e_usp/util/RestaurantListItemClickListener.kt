package com.punpuf.e_usp.util

import com.punpuf.e_usp.vo.Restaurant

interface RestaurantListItemClickListener {
    fun notifyRestaurantSelected(restaurantId: Int)
    fun openRestaurantInfo(restaurant: Restaurant)
}