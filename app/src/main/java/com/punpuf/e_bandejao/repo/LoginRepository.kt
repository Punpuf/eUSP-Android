package com.punpuf.e_bandejao.repo

import androidx.lifecycle.MediatorLiveData
import com.punpuf.e_bandejao.Const.Companion.ERROR_INTERNAL
import com.punpuf.e_bandejao.Const.Companion.ERROR_NETWORK
import com.punpuf.e_bandejao.Const.Companion.LOGIN_RETRY_DELAY_MILLIS
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN_SECRET
import com.punpuf.e_bandejao.Const.Companion.TABLE_TOKEN_VALUE_ID_WS_USER_ID
import com.punpuf.e_bandejao.db.TokenDao
import com.punpuf.e_bandejao.db.UserInfoDao
import com.punpuf.e_bandejao.network.UspNetworkService
import com.punpuf.e_bandejao.network.UspApi
import com.punpuf.e_bandejao.vo.Token
import com.punpuf.e_bandejao.vo_network.NetworkRequestBodySubscription
import com.github.scribejava.core.model.*
import com.github.scribejava.core.oauth.OAuth10aService
import com.punpuf.e_bandejao.vo.LoginAccessTokenBundle
import com.punpuf.e_bandejao.vo.LoginRequestTokenBundle
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber.d
import timber.log.Timber.e
import java.lang.Exception
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val oauthService: OAuth10aService,
    private val tokenDao: TokenDao,
    private val userInfoDao: UserInfoDao,
    private val uspNetworkService: UspNetworkService,
) {

    // Getting request token, then auth url
    val requestTokenData = MediatorLiveData<Resource<LoginRequestTokenBundle>>()
    suspend fun getRequestTokenBundleData() {
        withContext(Dispatchers.IO) {
            while (true) {
                requestTokenData.postValue(Resource.Loading())

                try {
                    d("getRequestTokenBundleData starting")
                    val requestToken = oauthService.requestToken
                    val authUrl = oauthService.getAuthorizationUrl(requestToken)
                    val bundle = LoginRequestTokenBundle(requestToken.token, requestToken.tokenSecret, authUrl)
                    requestTokenData.postValue(Resource.Success(bundle))
                    d("getRequestTokenBundleData SUCCESS")
                    return@withContext
                }
                catch (exception: Exception) {
                    e("getRequestTokenBundleData ERROR: $exception")
                    requestTokenData.postValue(Resource.Error(ERROR_NETWORK,null))
                    delay(LOGIN_RETRY_DELAY_MILLIS)
                }

            }
        }
    }

    // Gets access token, authenticate user (getting their info), subscribe/register user key
    // store response
    val accessTokenStateData = MediatorLiveData<Resource<String>>()
    suspend fun fetchAndStoreAccessToken(accessTokenBundle: LoginAccessTokenBundle) {
        withContext(Dispatchers.IO) {
            while (true) { // todo add a check to see if should still continue

                accessTokenStateData.postValue(Resource.Loading())

                // get access token
                val accessToken: OAuth1AccessToken
                try {
                    val requestToken = OAuth1RequestToken(accessTokenBundle.requestTokenToken, accessTokenBundle.requestTokenSecret)
                    accessToken = oauthService.getAccessToken(requestToken, accessTokenBundle.oauthVerifier)
                }
                catch (exception: Exception) {
                    e("Error getting accessToken: $exception")
                    accessTokenStateData.postValue(Resource.Error(ERROR_NETWORK))
                    delay(LOGIN_RETRY_DELAY_MILLIS)
                    continue
                }


                // authenticate user
                val oauthRequest = OAuthRequest(Verb.POST, UspApi.AUTHENTICATE_URL)
                oauthService.signRequest(accessToken, oauthRequest)
                val response: Response
                try {
                    response = oauthService.execute(oauthRequest)

                    d("Response just came in: $response\n\n\n")
                    d("Body just came in: ${response.body}\n\n\n")
                    d("Headers just came in: ${response.headers}\n\n\n")
                    d("Code just came in: ${response.code}\n\n\n ")
                }
                catch (exception: Exception) {
                    e("Error getting user information: $exception")
                    accessTokenStateData.postValue(Resource.Error(ERROR_NETWORK))
                    delay(LOGIN_RETRY_DELAY_MILLIS)
                    continue
                }

                // process response
                val userInfo: UserInfo
                val wsUserIdToken: Token
                try {
                    val jsonResponseBody = JSONObject(response.body)
                    wsUserIdToken = Token(TABLE_TOKEN_VALUE_ID_WS_USER_ID, jsonResponseBody.getString("wsuserid"))

                    val numberUsp = jsonResponseBody.getString("loginUsuario")
                    val userName = jsonResponseBody.getString("nomeUsuario")
                    val department = jsonResponseBody.getJSONArray("vinculo").getJSONObject(0)
                        .getString("nomeUnidade")
                    userInfo = UserInfo(numberUsp, userName, department)
                }
                catch (exception: Exception) {
                    e("Error processing json response: $exception")
                    accessTokenStateData.postValue(Resource.Error(ERROR_INTERNAL))
                    delay(LOGIN_RETRY_DELAY_MILLIS)
                    continue
                }

                // subscribe credentials
                try {
                    val subscribeResponse = uspNetworkService.subscribeUser(
                        NetworkRequestBodySubscription(wsUserIdToken.token ?: "")
                    )

                    d("Subscription of User Response: $subscribeResponse")
                    if (!response.isSuccessful) {
                        e("Error (non successful response) subscribing user")
                        accessTokenStateData.postValue(Resource.Error(ERROR_NETWORK))
                        delay(LOGIN_RETRY_DELAY_MILLIS)
                        continue
                    }
                }
                catch (exception: Exception) {
                    e("Error subscribing user: $exception")
                    accessTokenStateData.postValue(Resource.Error(ERROR_NETWORK))
                    delay(LOGIN_RETRY_DELAY_MILLIS)
                    continue
                }


                // save information
                val accessTokenToken = Token(TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN, accessToken.token)
                val accessTokenTokenSecret = Token(TABLE_TOKEN_VALUE_ID_ACCESS_TOKEN_TOKEN_SECRET, accessToken.tokenSecret)

                tokenDao.insertTokenList(listOf(accessTokenToken, accessTokenTokenSecret, wsUserIdToken))
                userInfoDao.insertUserInfo(userInfo)

                d("Access token and User info have been saved, hurray!")
                accessTokenStateData.postValue(Resource.Success(""))
                return@withContext
            }
        }
    }

}