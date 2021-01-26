package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.repo.LoginRepository
import com.punpuf.e_bandejao.vo.LoginAccessTokenBundle
import com.punpuf.e_bandejao.vo.LoginRequestTokenBundle
import com.punpuf.e_bandejao.vo.Resource
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val loginRepo: LoginRepository
): ViewModel() {

    val requestTokenBundle: LiveData<Resource<LoginRequestTokenBundle>> by lazy {
        viewModelScope.launch { loginRepo.getRequestTokenBundleData() }
        loginRepo.requestTokenData
    }

    private val accessTokenBundleData = MediatorLiveData<LoginAccessTokenBundle>()
    fun setAccessTokenBundle(loginAccessTokenBundle: LoginAccessTokenBundle) {
        if (loginAccessTokenBundle == accessTokenBundleData.value) return
        accessTokenBundleData.postValue(loginAccessTokenBundle)
    }

    val accessTokenState: LiveData<Resource<String>> = Transformations.switchMap(accessTokenBundleData) { accessTokenBundle ->
        viewModelScope.launch {
            loginRepo.fetchAndStoreAccessToken(accessTokenBundle)
        }
        loginRepo.accessTokenStateData
    }

}