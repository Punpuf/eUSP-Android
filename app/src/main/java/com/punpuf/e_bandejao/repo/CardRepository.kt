package com.punpuf.e_bandejao.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.Const.Companion.FILE_ID_PROFILE_PIC
import com.punpuf.e_bandejao.Const.Companion.FILE_NAME_PROFILE_PIC
import com.punpuf.e_bandejao.Const.Companion.NETWORK_MAX_RETRY_ATTEMPTS
import com.punpuf.e_bandejao.Const.Companion.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT
import com.punpuf.e_bandejao.Const.Companion.SHARED_PREFS_APP_LAST_FETCH_RESTAURANT_LIST
import com.punpuf.e_bandejao.Const.Companion.SHARED_PREFS_APP_LAST_USE
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_VALUE_ID_WS_USER_ID
import com.punpuf.e_bandejao.background.UpdateTokenWorkerHelper
import com.punpuf.e_bandejao.db.*
import com.punpuf.e_bandejao.network.ApiResponse
import com.punpuf.e_bandejao.network.CustomNetworkService
import com.punpuf.e_bandejao.network.NetworkBoundResource
import com.punpuf.e_bandejao.network.UspNetworkService
import com.punpuf.e_bandejao.vo.*
import com.punpuf.e_bandejao.vo_network.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber.d
import timber.log.Timber.e
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CardRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userProfileDao: UserProfileDao,
    private val tokenDao: TokenDao,
    private val userInfoDao: UserInfoDao,
    private val profilePictureInfoDao: ProfilePictureInfoDao,
    private val uspNetworkService: UspNetworkService,
    private val updateTokenWorkerHelper: UpdateTokenWorkerHelper,
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    init {
        sharedPreferences.edit().putLong(SHARED_PREFS_APP_LAST_USE, System.currentTimeMillis()).apply()
    }

    fun getUserInfo(): LiveData<UserInfo?> {
        return userInfoDao.getUserInfoData()
    }

    suspend fun getUserProfiles(): LiveData<Resource<List<UserProfile>>> {
        return object : NetworkBoundResource<List<UserProfile>, NetworkResponseListProfiles>() {

            override suspend fun loadFromDb(): LiveData<List<UserProfile>> {
                return userProfileDao.getAllUserProfilesData()
            }

            override suspend fun shouldFetch(data: List<UserProfile>?): Boolean {
                if (tokenDao.getTokenById(TABLE_TOKEN_VALUE_ID_WS_USER_ID) == null) return false
                if (data.isNullOrEmpty()) return true

                // check if there are any profiles with expired qrcode tokens
                for (userProfile in data) {
                    try {
                        val tokenExpirationDate = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        )
                            .parse(userProfile.qrCodeTokenExpiration ?: "")

                        val nowCalendar = Calendar.getInstance()
                        val tokenExpirationDateCalendar = Calendar.getInstance()
                         tokenExpirationDateCalendar.timeInMillis = tokenExpirationDate?.time ?: 0

                        if (nowCalendar.after(tokenExpirationDateCalendar)) return true
                    }
                    catch (exception: Exception) {
                        e("formating exeception: $exception")
                        return true
                    }
                }
                // no profile has expired qrcode token
                d("No need to update user profile list!!!")
                return false
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseListProfiles>> {
                d("getUserProfiles, createCall")
                val wsUserIdToken: Token? = tokenDao.getTokenById(TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return uspNetworkService.fetchUserProfileListData(
                    NetworkRequestBodyToken(
                        wsUserIdToken?.token ?: ""
                    )
                )
            }

            override suspend fun saveCallResult(result: NetworkResponseListProfiles) {
                d("saving list of users ${result.userProfileList}")
                if (result.hasError == false) {
                    userProfileDao.deleteAllUserProfiles()
                    userProfileDao.insertUserProfiles(result.userProfileList)
                    updateTokenWorkerHelper.enqueueUpdateWorkerRequest(ExistingWorkPolicy.REPLACE)
                }
            }

            override suspend fun onFetchFailed() {
                delay(5000)
                numNetworkRetries += 1
                if (numNetworkRetries <= NETWORK_MAX_RETRY_ATTEMPTS) build()
            }

        }.build()
    }

    suspend fun getProfilePictureInfo(): LiveData<Resource<ProfilePictureInfo?>> {
        return object : NetworkBoundResource<ProfilePictureInfo?, NetworkResponseProfilePicture>() {
            override suspend fun loadFromDb(): LiveData<ProfilePictureInfo?> {
                return profilePictureInfoDao.geProfileLocationPictureById(FILE_ID_PROFILE_PIC)
            }

            override suspend fun shouldFetch(data: ProfilePictureInfo?): Boolean {
                d("getProfilePictureInfo - shouldFetch $data")
                if (tokenDao.getTokenById(TABLE_TOKEN_VALUE_ID_WS_USER_ID) == null) return false
                if (data == null) return true
                d("getProfilePictureInfo - shouldFetch - 222222")
                val currentTime = System.currentTimeMillis()
                val previousDownloadTime = data.downloadDate
                val diff = currentTime - previousDownloadTime
                d(
                    "getProfilePictureInfo - shouldFetch - 333333 - ${
                        (TimeUnit.MILLISECONDS.toDays(
                            diff
                        ) > 7)
                    }}"
                )
                // refreshes if current pic is older than a week
                return (TimeUnit.MILLISECONDS.toDays(diff) > 7)
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseProfilePicture>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return uspNetworkService.fetchUserProfilePictureData(
                    NetworkRequestBodyToken(
                        wsUserIdToken?.token ?: ""
                    )
                )
            }

            @Suppress("BlockingMethodInNonBlockingContext")
            override suspend fun saveCallResult(result: NetworkResponseProfilePicture) {
                try {
                    deletePicture()

                    val fileOutputStream = context.openFileOutput(
                        FILE_NAME_PROFILE_PIC,
                        Context.MODE_PRIVATE
                    )
                    val decodedResponse = Base64.decode(result.encodedProfilePicture, Base64.DEFAULT)
                    fileOutputStream.write(decodedResponse)
                    fileOutputStream.close()
                    d("picture has been saved to internal storage")

                    val currentTime = System.currentTimeMillis()
                    profilePictureInfoDao.insertProfilePictureLocation(
                        ProfilePictureInfo(
                            FILE_ID_PROFILE_PIC,
                            FILE_NAME_PROFILE_PIC,
                            currentTime
                        )
                    )
                } catch (exception: Exception) {
                    e("Saving picture exception: $exception")
                }
            }
        }.build()
    }

    suspend fun logoutUser() {
        withContext(Dispatchers.IO) {
            try {
                val wsUserIdToken: Token? = tokenDao.getTokenById(TABLE_TOKEN_VALUE_ID_WS_USER_ID)

                userProfileDao.deleteAllUserProfiles()
                userInfoDao.deleteAllUserProfiles()
                profilePictureInfoDao.deleteAll()
                tokenDao.deleteAllTokens()

                deletePicture()

                updateTokenWorkerHelper.cancelUpdateWorker()

                uspNetworkService.unsubscribeUser(
                    NetworkRequestBodySubscription(
                        wsUserIdToken?.token ?: ""
                    )
                )
            } catch (exception: Exception) { e("unsub this error: $exception")}
        }
    }

    private fun deletePicture() {
        try {
            val fileOutputStream = context.openFileOutput(
                FILE_NAME_PROFILE_PIC,
                Context.MODE_PRIVATE
            )
            fileOutputStream.write(ByteArray(0))
            fileOutputStream.close()
        }
        catch (exception: Exception) {
            e("deleting picture exception: $exception")
        }
    }

}