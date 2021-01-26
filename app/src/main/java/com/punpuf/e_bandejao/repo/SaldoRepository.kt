package com.punpuf.e_bandejao.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.Const.Companion.SHARED_PREFS_APP_ACCOUNT_BALANCE
import com.punpuf.e_bandejao.db.BoletoDao
import com.punpuf.e_bandejao.db.TokenDao
import com.punpuf.e_bandejao.network.ApiResponse
import com.punpuf.e_bandejao.network.NetworkBoundResource
import com.punpuf.e_bandejao.network.UspNetworkService
import com.punpuf.e_bandejao.vo.Boleto
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.Token
import com.punpuf.e_bandejao.vo_network.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    suspend fun generateBoleto(amount: Double) {
        withContext(Dispatchers.IO) {
            try {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)

                val df = DecimalFormat("#.##")
                val response = uspNetworkService.fetchGeneratedBoleto(
                    NetworkRequestGenerateBoleto(
                        wsUserIdToken?.token ?: "",
                        df.format(amount)
                    )
                )
                if (response.isSuccessful && response.body()?.hasError != true) {
                    val body = response.body()
                    boletoDao.insertBoleto(Boleto(body?.id ?: "", body?.barcode, body?.amount, body?.expirationDate))
                }
                d("generateBoleto of ${df.format(amount)}, response is: ${response.body()}")
            } catch (exception: Exception) { Timber.e("generateBoleto error: $exception")
            }
        }
    }

    /*suspend fun viewOngoingBoleto() {
        withContext(Dispatchers.IO) {
            try {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                val response = uspNetworkService.fetchOngoingBoletoList(
                    NetworkRequestBodyToken(wsUserIdToken?.token ?: "")
                )
                Timber.d("viewOngoingBoleto response is: ${response.body()}")
            } catch (exception: Exception) { Timber.e("viewOngoingBoleto error: $exception")
            }
        }
    }*/

    suspend fun cancelBoleto(boletoId: String) {
        withContext(Dispatchers.IO) {
            try {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                val response = uspNetworkService.cancelBoleto(
                    NetworkRequestDeleteBoleto(wsUserIdToken?.token ?: "", boletoId)
                )
                boletoDao.deleteAll()
                d("cancelBoleto was successful?: ${response.isSuccessful}; response is: ${response.body()}")
            } catch (exception: Exception) { Timber.e("cancelBoleto error: $exception") }
        }
    }
}