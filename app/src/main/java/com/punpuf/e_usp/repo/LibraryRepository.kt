package com.punpuf.e_usp.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.db.BookOfSearchDao
import com.punpuf.e_usp.db.BookOfUserDao
import com.punpuf.e_usp.db.TokenDao
import com.punpuf.e_usp.db.UserInfoDao
import com.punpuf.e_usp.network.ApiResponse
import com.punpuf.e_usp.network.LibraryNetworkService
import com.punpuf.e_usp.network.NetworkBoundResource
import com.punpuf.e_usp.vo.*
import com.punpuf.e_usp.vo_network.NetworkResponseBookUser
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenDao: TokenDao,
    private val userInfoDao: UserInfoDao,
    private val bookOfUserDao: BookOfUserDao,
    private val bookOfSearchDao: BookOfSearchDao,
    private val libraryNetworkService: LibraryNetworkService,
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Const.SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getUserInfo(): LiveData<UserInfo?> {
        return userInfoDao.getUserInfoData()
    }

    suspend fun getLibraryLoans(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookOfUserDao.getBookUsersByType(BookOfUserType.LOAN)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean {
                if (data == null) return true

                val now = System.currentTimeMillis()
                val lastFetch = sharedPreferences.getLong(Const.SHARED_PREFS_APP_LAST_FETCH_LIBRARY_LOANS, now)

                return TimeUnit.MILLISECONDS.toMinutes(now - lastFetch) > 60
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryLoans(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookOfUserDao.deleteBookUsersByType(BookOfUserType.LOAN)

                if ((result.numResults ?: 0) <= 0 || result.loanList?.isNullOrEmpty() == true) return
                for (bookUser in result.loanList) bookUser.type = BookOfUserType.LOAN
                bookOfUserDao.insertBookUsers(result.loanList)
            }

        }.build()
    }

    suspend fun getLibraryReservations(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookOfUserDao.getBookUsersByType(BookOfUserType.RESERVATION)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean {
                if (data == null) return true

                val now = System.currentTimeMillis()
                val lastFetch = sharedPreferences.getLong(Const.SHARED_PREFS_APP_LAST_FETCH_LIBRARY_RESERVATION, now)

                return TimeUnit.MILLISECONDS.toMinutes(now - lastFetch) > 60
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryReservations(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookOfUserDao.deleteBookUsersByType(BookOfUserType.RESERVATION)

                if ((result.numResults ?: 0) <= 0 || result.reservationList?.isNullOrEmpty() == true) return
                for (bookUser in result.reservationList) bookUser.type = BookOfUserType.RESERVATION
                bookOfUserDao.insertBookUsers(result.reservationList)
            }

        }.build()
    }

    suspend fun getLibraryHistory(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookOfUserDao.getBookUsersByType(BookOfUserType.HISTORY)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean {
                if (data == null) return true

                val now = System.currentTimeMillis()
                val lastFetch = sharedPreferences.getLong(Const.SHARED_PREFS_APP_LAST_FETCH_LIBRARY_HISTORY, now)

                return TimeUnit.MILLISECONDS.toMinutes(now - lastFetch) > 60
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryHistory(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookOfUserDao.deleteBookUsersByType(BookOfUserType.HISTORY)

                if ((result.numResults ?: 0) <= 0 || result.historyList?.isNullOrEmpty() == true) return
                for (bookUser in result.historyList) bookUser.type = BookOfUserType.HISTORY
                bookOfUserDao.insertBookUsers(result.historyList)
            }

        }.build()
    }

    fun getSearchStuff(query: String): PagingSource<Int, BookOfSearch> {
        return bookOfSearchDao.pagingSource(query)
    }
}