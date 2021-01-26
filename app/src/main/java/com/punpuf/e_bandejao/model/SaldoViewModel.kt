package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.repo.CardRepository
import com.punpuf.e_bandejao.repo.SaldoRepository
import com.punpuf.e_bandejao.util.AbsentLiveData
import com.punpuf.e_bandejao.vo.Boleto
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.UserInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.d

class SaldoViewModel @ViewModelInject constructor(
    private val saldoRepo: SaldoRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    val userInfo: LiveData<UserInfo?> = cardRepository.getUserInfo()

    private val accountBalanceData = MediatorLiveData<Resource<String?>>()
    private var accountBalanceJob: Job? = null
    val accountBalance: LiveData<Resource<String?>> = Transformations.switchMap(userInfo) { user ->
        if (user == null) { AbsentLiveData.create<Resource<String?>>() }
        else {
            accountBalanceJob?.cancel()
            accountBalanceJob = viewModelScope.launch {
                accountBalanceData.addSource(saldoRepo.fetchAccountBalance()) {
                    d("accountBalance - update $it")
                    accountBalanceData.postValue(it)
                }
            }
            accountBalanceData
        }
    }

    private val ongoingBoletoData = MediatorLiveData<Resource<Boleto?>>()
    private var ongoingBoletoJob: Job? = null
    val ongoingBoleto: LiveData<Resource<Boleto?>> = Transformations.switchMap(userInfo) { user ->
        if (user == null) { AbsentLiveData.create<Resource<Boleto?>>() }
        else {
            ongoingBoletoJob?.cancel()
            ongoingBoletoJob = viewModelScope.launch {
                ongoingBoletoData.addSource(saldoRepo.fetchOngoingBoleto()) {
                    ongoingBoletoData.postValue(it)
                }
            }
            ongoingBoletoData
        }
    }

    private var deleteBoletoJob: Job? = null
    fun deleteBoleto(id: String) {
        deleteBoletoJob?.cancel()
        deleteBoletoJob = viewModelScope.launch {
            saldoRepo.cancelBoleto(id)
        }
    }

    private var logoutUserJob: Job? = null
    fun logoutUser() {
        logoutUserJob?.cancel()
        logoutUserJob = viewModelScope.launch {
            cardRepository.logoutUser()
        }
    }

    private var generateBoletoJob: Job? = null
    fun generateBoleto(amount: Double) {
        generateBoletoJob?.cancel()
        generateBoletoJob = viewModelScope.launch {
            saldoRepo.generateBoleto(amount)
        }
    }



}