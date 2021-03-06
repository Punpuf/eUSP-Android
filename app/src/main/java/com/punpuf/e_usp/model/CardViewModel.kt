package com.punpuf.e_usp.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_usp.util.AbsentLiveData
import com.punpuf.e_usp.repo.CardRepository
import com.punpuf.e_usp.vo.ProfilePictureInfo
import com.punpuf.e_usp.vo.Resource
import com.punpuf.e_usp.vo.UserInfo
import com.punpuf.e_usp.vo.UserProfile
import kotlinx.coroutines.launch
import timber.log.Timber.d

class CardViewModel @ViewModelInject constructor(
    private val cardRepository: CardRepository,
) : ViewModel() {

    val userInfo: LiveData<UserInfo?> = cardRepository.getUserInfo()

    private val userProfileListData = MediatorLiveData<Resource<List<UserProfile>>>()
    private val userProfilePictureData = MediatorLiveData<Resource<ProfilePictureInfo?>>()
    val userProfileList: LiveData<Resource<List<UserProfile>>> = Transformations.switchMap(userInfo) { userInfo ->
        d("userProfileListData - init: $userInfo")
        if (userInfo == null) { AbsentLiveData.create<Resource<List<UserProfile>>>() }
        else {
            viewModelScope.launch {
                userProfileListData.addSource(cardRepository.getUserProfiles()) {
                    d("userProfileListData - update getUserProfiles: ${it?.data}")
                    userProfileListData.postValue(it)
                }
            }
            userProfileListData
        }
    }

    val userProfilePicture: LiveData<Resource<ProfilePictureInfo?>> = Transformations.switchMap(userInfo) { userInfo ->
        d("userProfilePictureData - init: $userInfo")
        if (userInfo == null) { AbsentLiveData.create<Resource<ProfilePictureInfo?>>() }
        else {
            viewModelScope.launch {
                userProfilePictureData.addSource(cardRepository.getProfilePictureInfo()) {
                    userProfilePictureData.postValue(it)
                }
            }
            userProfilePictureData
        }
    }

    private var shouldAutoOpenQrcode: Boolean? = null
    private var shouldAutoOpenBarcode: Boolean? = null
    private var shouldAutoOpenSetTime: Long = 0

    fun setAutoOpenQrcode(shouldOpen: Boolean) {
        if (shouldOpen && shouldAutoOpenQrcode == null) {
            shouldAutoOpenQrcode = true
            shouldAutoOpenSetTime = System.currentTimeMillis()
            return
        }
        if (!shouldOpen) shouldAutoOpenQrcode = false
    }

    fun setAutoOpenBarcode(shouldOpen: Boolean) {
        if (shouldOpen && shouldAutoOpenBarcode == null) {
            shouldAutoOpenBarcode = true
            shouldAutoOpenSetTime = System.currentTimeMillis()
            return
        }
        if (!shouldOpen) shouldAutoOpenBarcode = false
    }

    fun getAutoOpenQrcode() = shouldAutoOpenQrcode
    fun getAutoOpenBarcode() = shouldAutoOpenBarcode
    fun getAutoOpenSetTime() = shouldAutoOpenSetTime


    /*fun testOperations() {
        d("GOING TO TEST SOMETHING -------------------------")
        viewModelScope.launch {
            //cardRepository.fetchRestaurantList()
        }
    }*/

}