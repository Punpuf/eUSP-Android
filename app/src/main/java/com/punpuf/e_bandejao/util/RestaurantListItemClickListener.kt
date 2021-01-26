package com.punpuf.e_bandejao.util

import com.punpuf.e_bandejao.vo.Restaurant

interface RestaurantListItemClickListener {
    fun notifyRestaurantSelected(restaurantId: Int)
    fun openRestaurantInfo(restaurant: Restaurant)
}