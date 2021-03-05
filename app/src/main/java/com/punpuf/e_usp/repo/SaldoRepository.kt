package com.punpuf.e_usp.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.Const.Companion.SHARED_PREFS_APP_ACCOUNT_BALANCE
import com.punpuf.e_usp.db.BoletoDao
import com.punpuf.e_usp.db.TokenDao
import com.punpuf.e_usp.network.*
import com.punpuf.e_usp.vo.Boleto
import com.punpuf.e_usp.vo.Resource
import com.punpuf.e_usp.vo.Token
import com.punpuf.e_usp.vo_network.*
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber.d
import java.text.DecimalFormat
import javax.inject.Inject

class SaldoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenDao: TokenDao,
    private val boletoDao: BoletoDao,
    private val uspNetworkService: UspNetworkService,
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    suspend fun fetchAccountBalance(): LiveData<Resource<String?>> {
        return object : NetworkBoundResource<String?, NetworkResponseAccountBalance>() {
            override suspend fun loadFromDb(): LiveData<String?> {
                val data = MediatorLiveData<String?>()
                val saved = sharedPreferences.getString(SHARED_PREFS_APP_ACCOUNT_BALANCE, null)
                data.postValue(saved)
                return data
            }

            override suspend fun shouldFetch(data: String?): Boolean = true

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseAccountBalance>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return uspNetworkService.fetchAccountBalance(
                    NetworkRequestBodyToken(wsUserIdToken?.token ?: ""))
            }

            override suspend fun saveCallResult(result: NetworkResponseAccountBalance) {
                sharedPreferences.edit().putString(SHARED_PREFS_APP_ACCOUNT_BALANCE, result.accountBalance).apply()
            }

        }.build()
    }

    suspend fun fetchOngoingBoleto(): LiveData<Resource<Boleto?>> {
        return object : NetworkBoundResource<Boleto?, NetworkResponseOngoingBoletoList>() {

            override suspend fun loadFromDb(): LiveData<Boleto?> = boletoDao.getBoleto()

            override suspend fun shouldFetch(data: Boleto?): Boolean = true

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseOngoingBoletoList>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return uspNetworkService.fetchOngoingBoletoList(
                    NetworkRequestBodyToken(wsUserIdToken?.token ?: ""))
            }

            override suspend fun saveCallResult(result: NetworkResponseOngoingBoletoList) {
                if (result.hasError == true) {
                    d("Boleto Fetch response has error, interrupting")
                    return
                }

                boletoDao.deleteAll()
                if (result.boletoList.isNotEmpty()) {
                    d("Going to save the following Boleto: ${result.boletoList[0]}")
                    boletoDao.insertBoleto(result.boletoList[0])
                }
            }

        }.build()
    }

    suspend fun generateBoleto(amount: Double): LiveData<Resource<Boleto?>> {
        val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)

        return object : BoletoCreateOpResource<Boleto?, NetworkResponseOngoingBoletoList>() {
            override suspend fun loadFromDb(): LiveData<Boleto?> = boletoDao.getBoleto()

            override suspend fun createBoletoCall(): LiveData<ApiResponse<NetworkResponseGenerateBoleto>> {
                val df = DecimalFormat("#.##")
                return uspNetworkService.fetchGeneratedBoleto(
                    NetworkRequestGenerateBoleto(
                        wsUserIdToken?.token ?: "",
                        df.format(amount)
                    )
                )
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseOngoingBoletoList>> {
                return uspNetworkService.fetchOngoingBoletoList(
                    NetworkRequestBodyToken(wsUserIdToken?.token ?: ""))
            }

            override suspend fun saveCallResult(result: NetworkResponseOngoingBoletoList) {
                boletoDao.deleteAll()

                if (result.hasError == false && result.boletoList.isNotEmpty()) {
                    d("Going to save the following Boleto: ${result.boletoList[0]}")
                    boletoDao.insertBoleto(result.boletoList[0])
                }
            }

            override suspend fun saveCreatedBoletoResult(result: NetworkResponseGenerateBoleto) {
                boletoDao.deleteAll()
                boletoDao.insertBoleto(Boleto(result.id!!, result.barcode, result.amount, result.expirationDate))
            }

        }.build()

    }

    suspend fun cancelBoleto(boletoId: String): LiveData<Resource<Boleto?>> {
        val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)

        return object : BoletoDeleteOpResource<Boleto?, NetworkResponseOngoingBoletoList>() {
            override suspend fun loadFromDb(): LiveData<Boleto?> = boletoDao.getBoleto()

            override suspend fun createGetCall(): LiveData<ApiResponse<NetworkResponseOngoingBoletoList>> {
                return uspNetworkService.fetchOngoingBoletoList(
                    NetworkRequestBodyToken(wsUserIdToken?.token ?: ""))
            }

            override suspend fun createDeleteCall(): LiveData<ApiResponse<String>> {
                return uspNetworkService.cancelBoleto(
                    NetworkRequestDeleteBoleto(wsUserIdToken?.token ?: "", boletoId)
                )
            }

            override suspend fun saveCallResult(result: NetworkResponseOngoingBoletoList) {
                boletoDao.deleteAll()
                if (result.hasError == false && result.boletoList.isNotEmpty()) {
                    d("Going to save the following Boleto: ${result.boletoList[0]}")
                    boletoDao.insertBoleto(result.boletoList[0])
                }
            }

        }.build()
    }
}