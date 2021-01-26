package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.util.AbsentLiveData
import com.punpuf.e_bandejao.repo.CardRepository
import com.punpuf.e_bandejao.vo.ProfilePictureInfo
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.UserInfo
import com.punpuf.e_bandejao.vo.UserProfile
import kotlinx.coroutines.launch
import timber.log.Timber.d

class CardViewModel @ViewModelInject constructor(
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val userProfileListData = MediatorLiveData<Resource<List<UserProfile>>>()
    private val userProfilePictureData = MediatorLiveData<Resource<ProfilePictureInfo?>>()

    val userInfo: LiveData<UserInfo?> = cardRepository.getUserInfo()

    val userProfileList: LiveData<Resource<List<UserProfile>>> = Transformations.switchMap(userInfo) { userInfo ->
        d("userProfileListData - init: $userInfo")
        if (userInfo == null) { AbsentLiveData.create<Resource<List<UserProfile>>>() }
        else {
            viewModelScope.launch {
                userProfileListData.addSource(cardRepository.getUserProfiles()) {
                    d("userProfileListData - update getUserProfiles")
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

    fun logoutUser() {
        viewModelScope.launch {
            cardRepository.logoutUser()
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


    fun testOperations() {
        d("GOING TO TEST SOMETHING -------------------------")
        viewModelScope.launch {
            //cardRepository.fetchRestaurantList()
        }
    }

}