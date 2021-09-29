package com.punpuf.e_usp.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.background.UpdateTokenWorkerHelper
import com.punpuf.e_usp.db.*
import com.punpuf.e_usp.network.UspNetworkService
import com.punpuf.e_usp.vo.Token
import com.punpuf.e_usp.vo.UserInfo
import com.punpuf.e_usp.vo_network.NetworkRequestBodySubscription
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userProfileDao: UserProfileDao,
    private val tokenDao: TokenDao,
    private val userInfoDao: UserInfoDao,
    private val profilePictureInfoDao: ProfilePictureInfoDao,
    private val boletoDao: BoletoDao,
    private val bookOfUserDao: BookOfUserDao,
    private val uspNetworkService: UspNetworkService,
    private val updateTokenWorkerHelper: UpdateTokenWorkerHelper,
) {

    fun getUserInfo(): LiveData<UserInfo?> {
        return userInfoDao.getUserInfoData()
    }

    suspend fun logoutUser() {
        withContext(Dispatchers.IO) {
            try {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)

                userProfileDao.deleteAllUserProfiles()
                userInfoDao.deleteAllUserProfiles()
                profilePictureInfoDao.deleteAll()
                tokenDao.deleteAllTokens()
                boletoDao.deleteAll()
                bookOfUserDao.deleteAll()


                deletePicture()

                updateTokenWorkerHelper.cancelUpdateWorker()

                uspNetworkService.unsubscribeUser(
                    NetworkRequestBodySubscription(
                        wsUserIdToken?.token ?: ""
                    )
                )
            } catch (exception: Exception) { Timber.e("unsub this error: $exception")
            }
        }
    }

    private fun deletePicture() {
        try {
            val fileOutputStream = context.openFileOutput(
                Const.FILE_NAME_PROFILE_PIC,
                Context.MODE_PRIVATE
            )
            fileOutputStream.write(ByteArray(0))
            fileOutputStream.close()
        }
        catch (exception: Exception) {
            Timber.e("deleting picture exception: $exception")
        }
    }
}