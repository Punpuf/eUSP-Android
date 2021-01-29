package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.punpuf.e_bandejao.repo.CardRepository
import com.punpuf.e_bandejao.repo.SaldoRepository
import com.punpuf.e_bandejao.util.AbsentLiveData
import com.punpuf.e_bandejao.vo.Boleto
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.SaldoState
import com.punpuf.e_bandejao.vo.UserInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber.d

class SaldoViewModel @ViewModelInject constructor(
    private val saldoRepo: SaldoRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    //User data: refreshes ongoing boleto data if user "logs in"
    private val userInfoData = MediatorLiveData<UserInfo?>()
    private var userInfoOld: UserInfo? = null
    fun getUserInfo(): LiveData<UserInfo?> {
        userInfoData.addSource(cardRepository.getUserInfo()) {
            userInfoData.postValue(it)
            if (userInfoOld == null && it != null) newBoletoOp(SaldoState.OP_TYPE.GET)
            if (it == null) newBoletoOp(SaldoState.OP_TYPE.NO_USER)
            userInfoOld = it
        }
        return userInfoData
    }

    // Boleto Ops
    private var nextBoletoOpId = 0
    private fun newBoletoOp(opType: SaldoState.OP_TYPE, boletoId: String = "", amount: Double = 0.0) {
        d("Received new OP; $opType")
        ongoingBoletoOp.postValue(SaldoState(opType, nextBoletoOpId, extraId = boletoId, extraAmount = amount,))
        nextBoletoOpId++
    }
    private val ongoingBoletoOp = MediatorLiveData<SaldoState>()

    //Ongoing Boleto
    private val ongoingBoletoData = MediatorLiveData<Resource<Boleto?>>()
    private var ongoingBoletoSource : LiveData<Resource<Boleto?>>? = null
    private var ongoingBoletoJob: Job? = null
    val ongoingBoleto: LiveData<Resource<Boleto?>> = Transformations.switchMap(ongoingBoletoOp) { op ->
        // end previous jobs
        ongoingBoletoJob?.cancel()
        if (ongoingBoletoSource != null) ongoingBoletoData.removeSource(ongoingBoletoSource!!)

        ongoingBoletoJob = viewModelScope.launch {
            ongoingBoletoSource = when(op.opType) {
                SaldoState.OP_TYPE.GET -> { saldoRepo.fetchOngoingBoleto() }
                SaldoState.OP_TYPE.CREATE -> { saldoRepo.generateBoleto(op.extraAmount) }
                SaldoState.OP_TYPE.DELETE -> { saldoRepo.cancelBoleto(op.extraId) }
                SaldoState.OP_TYPE.NO_USER -> { AbsentLiveData.create() }
            }
            ongoingBoletoData.addSource(ongoingBoletoSource!!) {
                ongoingBoletoData.postValue(it)
            }
        }

        ongoingBoletoData
    }

    // Boleto actions
    fun deleteBoleto(id: String) {
        newBoletoOp(SaldoState.OP_TYPE.DELETE, boletoId = id)
    }

    fun refreshBoleto() {
        newBoletoOp(SaldoState.OP_TYPE.GET)
    }

    fun generateBoleto(amount: Double) {
        newBoletoOp(SaldoState.OP_TYPE.CREATE, amount = amount)
    }

    // Account Balance
    private val accountBalanceData = MediatorLiveData<Resource<String?>>()
    private var ongoingAccountBalanceSource : LiveData<Resource<String?>>? = null
    private var accountBalanceJob: Job? = null
    val accountBalance: LiveData<Resource<String?>> = Transformations.switchMap(ongoingBoletoOp) { op ->
        if (op.opType == SaldoState.OP_TYPE.GET) {
            // end previous work
            accountBalanceJob?.cancel()
            if (ongoingAccountBalanceSource != null) accountBalanceData.removeSource(ongoingAccountBalanceSource!!)

            // get updated data
            accountBalanceJob = viewModelScope.launch {
                ongoingAccountBalanceSource = saldoRepo.fetchAccountBalance()
                accountBalanceData.addSource(ongoingAccountBalanceSource!!) {
                    d("accountBalance - update $it")
                    accountBalanceData.postValue(it)
                }
            }
        }
        else if (op.opType == SaldoState.OP_TYPE.NO_USER) {
            // end previous work
            accountBalanceJob?.cancel()
            if (ongoingAccountBalanceSource != null) accountBalanceData.removeSource(ongoingAccountBalanceSource!!)

            // absent data source
            ongoingAccountBalanceSource = AbsentLiveData.create()
            accountBalanceData.addSource(ongoingAccountBalanceSource!!) { accountBalanceData.postValue(it) }
        }

        accountBalanceData
    }

}