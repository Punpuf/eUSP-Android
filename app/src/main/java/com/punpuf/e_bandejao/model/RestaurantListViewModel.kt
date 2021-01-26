package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.repo.RestaurantListRepository
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.Restaurant
import kotlinx.coroutines.launch

class RestaurantListViewModel @ViewModelInject constructor(
    private val repo: RestaurantListRepository
) : ViewModel() {

    private val restaurantListData = MediatorLiveData<Resource<List<Restaurant>>>()

    fun getRestaurantList() : LiveData<Resource<List<Restaurant>>> {
            viewModelScope.launch {
                restaurantListData.addSource(repo.getRestaurantList()) {
                    restaurantListData.postValue(it)
                }
            }
        return restaurantListData
    }

    fun setSelectedRestaurant(restaurantId: Int) {
        viewModelScope.launch {
            repo.setSelectedRestaurant(restaurantId)
        }
    }

}