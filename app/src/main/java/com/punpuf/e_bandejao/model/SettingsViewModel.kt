package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punpuf.e_bandejao.repo.SettingsRepository
import com.punpuf.e_bandejao.vo.UserInfo
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