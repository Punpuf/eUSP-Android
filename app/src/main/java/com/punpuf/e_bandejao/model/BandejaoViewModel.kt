package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.Const.Companion.DEFAULT_BANDEJAO_ID
import com.punpuf.e_bandejao.repo.BandejaoRepository
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.Restaurant
import com.punpuf.e_bandejao.vo.SelectedRestaurant
import com.punpuf.e_bandejao.vo.WeeklyMenu
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber.d

class BandejaoViewModel @ViewModelInject constructor(
    private val bandejaoRepo: BandejaoRepository,
) : ViewModel() {

    private val selectedRestaurant = bandejaoRepo.getSelectedRestaurant()

    var tabLayoutPosition: Int? = null
    var prevRestaurant: SelectedRestaurant? = null

    private val currentRestaurantData = MediatorLiveData<Resource<Restaurant?>>()
    val currentRestaurant: LiveData<Resource<Restaurant?>> = Transformations.switchMap(selectedRestaurant) { selectedRestaurant ->
        viewModelScope.launch {
            d("current restaurant coroutine launched; id: ${selectedRestaurant?.id ?: DEFAULT_BANDEJAO_ID}")
            currentRestaurantData.addSource(
                bandejaoRepo.fetchRestaurantById(selectedRestaurant?.id ?: DEFAULT_BANDEJAO_ID)
            ) {
                d("currentRestaurantData: source update")
                currentRestaurantData.postValue(it)
            }
        }
        d("current restaurant returned")
        currentRestaurantData
    }

    private var job: Job? = null
    private var lastSelectedRestaurantId: Int? = null
    private val weeklyMenuData = MediatorLiveData<Resource<WeeklyMenu?>>()
    val weeklyMenu: LiveData<Resource<WeeklyMenu?>> = Transformations.switchMap(selectedRestaurant) { selectedRestaurant ->
        lastSelectedRestaurantId = selectedRestaurant?.id

        // so that init page is set when data changes
        if (prevRestaurant != null && prevRestaurant != selectedRestaurant) tabLayoutPosition = null
        prevRestaurant = selectedRestaurant

        job?.cancel()
        job = viewModelScope.launch {
            weeklyMenuData.addSource(
                bandejaoRepo.fetchRestaurantMenu(selectedRestaurant?.id ?: DEFAULT_BANDEJAO_ID)
            ) {
                d("weeklyMenu -- ${it.status}")
                if (lastSelectedRestaurantId == it?.data?.restaurantId) weeklyMenuData.postValue(it)
            }
        }
        weeklyMenuData

    }

}