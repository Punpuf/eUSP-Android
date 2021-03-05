package com.punpuf.e_usp.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punpuf.e_usp.repo.SettingsRepository
import com.punpuf.e_usp.vo.UserInfo
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val settingsRepo: SettingsRepository,

): ViewModel() {

    val userInfo: LiveData<UserInfo?> = settingsRepo.getUserInfo()

    fun logoutUser() {
        viewModelScope.launch {
            settingsRepo.logoutUser()
        }
    }
}