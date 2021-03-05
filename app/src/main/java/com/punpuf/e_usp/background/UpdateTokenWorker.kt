package com.punpuf.e_usp.background

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkerParameters
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.Const.Companion.SHARED_PREFS_APP_LAST_USE
import com.punpuf.e_usp.Const.Companion.SHARED_PREFS_NAME
import com.punpuf.e_usp.Const.Companion.WORKER_UPDATE_TOKEN_MAX_INACTIVE_APP_DAYS
import com.punpuf.e_usp.db.TokenDao
import com.punpuf.e_usp.db.UserInfoDao
import com.punpuf.e_usp.db.UserProfileDao
import com.punpuf.e_usp.util.Utils.Companion.sendNotificationWithMsg
import com.punpuf.e_usp.vo_network.NetworkRequestBodyToken
import com.punpuf.e_usp.vo_network.NetworkResponseListProfiles
import com.punpuf.e_usp.vo.UserProfile
import com.punpuf.e_usp.network.UspNetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import timber.log.Timber.d
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UpdateTokenWorker @WorkerInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val userInfoDao: UserInfoDao,
    private val userProfileDao: UserProfileDao,
    private val tokenDao: TokenDao,
    private val uspNetworkService: UspNetworkService,
    private val updateTokenWorkerHelper: UpdateTokenWorkerHelper
    ) : CoroutineWorker(appContext, workerParams) {

    /**
     * Attempts to update User's Profile List (and QR code info).
     * @return Result.success() if update was successful or not required.
     * @return Result.retry() if there was a network failure.
     * @return Result.failure() if missing required info, or response has error.
     */
    override suspend fun doWork(): Result = coroutineScope {
        withContext(Dispatchers.IO) {
            // If app isn't being used, update not required
            if (isAppInactive()) {
                d("do Work -> App is inactive")
                sendNotificationWithMsg(appContext,"do Work -> App is inactive")
                return@withContext Result.success()
            }

            // Checks if user is logged in
            val userInfo = userInfoDao.getUserInfo()
            if (userInfo == null) {
                d("do Work -> User not logged in")
                sendNotificationWithMsg(appContext,"do Work -> User not logged in")
                return@withContext Result.failure()
            }

            // Checks if current data requires an update
            val userProfileList = userProfileDao.getAllUserProfiles()
            if (!shouldRefreshData(userProfileList)) {
                d("do Work -> No need to update user's profiles")
                sendNotificationWithMsg(appContext,"do Work -> No need to update user's profiles")
                return@withContext Result.success()
            }

            // Checks if stored Token is valid
            val wsUserIdToken = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
            if (wsUserIdToken == null) {
                d("do Work -> Token not available")
                sendNotificationWithMsg(appContext,"do Work -> Token not available")
                return@withContext Result.failure()
            }

            // Makes network call, and checks response's validity
            val response: Response<NetworkResponseListProfiles>
            try {
                response = uspNetworkService.fetchUserProfileList(NetworkRequestBodyToken(wsUserIdToken.token!!))
                sendNotificationWithMsg(appContext,"do Work -> Network Response is $response")
                if (!response.isSuccessful){
                    d("do Work -> Bad Network Response")
                    sendNotificationWithMsg(appContext,"do Work -> Bad Network Response")
                    return@withContext Result.retry()
                }

            // Catches normally no network connectivity
            } catch (exception: Exception) {
                d("do Work -> Exception on Network Response")
                sendNotificationWithMsg(appContext,"do Work -> Exception on Network Response")
                return@withContext Result.retry()
            }

            // Checks if server responded with an error
            val responseData = response.body()
            if (responseData?.hasError == true || responseData?.userProfileList.isNullOrEmpty()) {
                d("do Work -> Response indicated an error: ${responseData?.errorMsg}")
                sendNotificationWithMsg(appContext,"do Work -> Response indicated an error: ${responseData?.errorMsg}")
                return@withContext Result.failure()
            }

            // Saves new data to internal storage
            userProfileDao.deleteAllUserProfiles()
            userProfileDao.insertUserProfiles(responseData?.userProfileList ?: emptyList())
            sendNotificationWithMsg(appContext,"Tudo certo patrao, atualizamos as info!")
            updateTokenWorkerHelper.enqueueUpdateWorkerRequest(ExistingWorkPolicy.APPEND_OR_REPLACE)
            Result.success()
        }
    }

    private fun shouldRefreshData(userProfileList: List<UserProfile>): Boolean {
        if (userProfileList.isNullOrEmpty()) return true
        try {
            // check if there are any profiles with expired qrcode tokens
            for (userProfile in userProfileList) {
                val tokenExpirationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .parse(userProfile.qrCodeTokenExpiration ?: "")
                val tokenExpirationDateCalendar = Calendar.getInstance()
                tokenExpirationDateCalendar.timeInMillis = tokenExpirationDate?.time ?: 0
                val nowCalendar = Calendar.getInstance()

                if (nowCalendar.after(tokenExpirationDateCalendar)) return true
            }
            return false
        }
        catch (exception: Exception) {
            Timber.e("formating exeception: $exception")
            return true
        }
    }

    private fun isAppInactive(): Boolean {
        val sharedPreferences = appContext.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val diff = System.currentTimeMillis() - sharedPreferences.getLong(SHARED_PREFS_APP_LAST_USE, 0)
        return TimeUnit.MILLISECONDS.toDays(diff) > WORKER_UPDATE_TOKEN_MAX_INACTIVE_APP_DAYS
    }

}