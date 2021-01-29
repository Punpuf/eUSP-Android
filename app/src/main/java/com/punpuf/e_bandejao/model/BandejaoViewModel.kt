package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.Const.Companion.DEFAULT_BANDEJAO_ID
import com.punpuf.e_bandejao.Const.Companion.ERROR_INTERNAL
import com.punpuf.e_bandejao.repo.BandejaoRepository
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.Restaurant
import com.punpuf.e_bandejao.vo.SelectedRestaurant
import com.punpuf.e_bandejao.vo.WeeklyMenu
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BandejaoViewModel @ViewModelInject constructor(
    private val bandejaoRepo: BandejaoRepository,
) : ViewModel() {

    private val selectedRestaurant = bandejaoRepo.getSelectedRestaurant()

    var tabLayoutPosition: Int? = null
    private var prevRestaurant: SelectedRestaurant? = null

    // Restaurant obj from id of selected restaurant
    private val currentRestaurantData = MediatorLiveData<Resource<Restaurant?>>()
    val currentRestaurant: LiveData<Resource<Restaurant?>> = Transformations.switchMap(selectedRestaurant) { selectedRestaurant ->
        viewModelScope.launch {
            currentRestaurantData.addSource(
                bandejaoRepo.fetchRestaurantById(selectedRestaurant?.id ?: DEFAULT_BANDEJAO_ID)
            ) {
                currentRestaurantData.postValue(it)
            }
        }
        currentRestaurantData
    }

    // Weekly menu data of selected restaurant
    private var lastSelectedRestaurantId: Int? = null
    private val weeklyMenuData = MediatorLiveData<Resource<WeeklyMenu?>>()
    private var weeklyMenuSource : LiveData<Resource<WeeklyMenu?>>? = null
    private var weeklyMenuJob: Job? = null
    val weeklyMenu: LiveData<Resource<WeeklyMenu?>> = Transformations.switchMap(selectedRestaurant) { selectedRestaurant ->
        lastSelectedRestaurantId = selectedRestaurant?.id ?:DEFAULT_BANDEJAO_ID

        // so that init page is set when data changes
        if (prevRestaurant != null && prevRestaurant != selectedRestaurant) tabLayoutPosition = null
        prevRestaurant = selectedRestaurant

        // cancel previous work
        weeklyMenuJob?.cancel()
        if (weeklyMenuSource != null) weeklyMenuData.removeSource(weeklyMenuSource!!)

        // get updated data
        weeklyMenuJob = viewModelScope.launch {
            weeklyMenuSource = bandejaoRepo.fetchRestaurantMenu(selectedRestaurant?.id ?: DEFAULT_BANDEJAO_ID)
            weeklyMenuData.addSource(weeklyMenuSource!!) {
                if (lastSelectedRestaurantId == it?.data?.restaurantId) weeklyMenuData.postValue(it)
                else weeklyMenuData.postValue(Resource.Error(ERROR_INTERNAL))
            }
        }

        weeklyMenuData
    }

}