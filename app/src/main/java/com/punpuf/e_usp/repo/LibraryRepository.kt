package com.punpuf.e_usp.repo

import androidx.lifecycle.LiveData
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.db.BookUserDao
import com.punpuf.e_usp.db.TokenDao
import com.punpuf.e_usp.db.UserInfoDao
import com.punpuf.e_usp.network.ApiResponse
import com.punpuf.e_usp.network.LibraryNetworkService
import com.punpuf.e_usp.network.NetworkBoundResource
import com.punpuf.e_usp.vo.*
import com.punpuf.e_usp.vo_network.NetworkResponseBookUser
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val tokenDao: TokenDao,
    private val userInfoDao: UserInfoDao,
    private val bookUserDao: BookUserDao,
    private val libraryNetworkService: LibraryNetworkService,
) {

    fun getUserInfo(): LiveData<UserInfo?> {
        return userInfoDao.getUserInfoData()
    }

    suspend fun getLibraryLoans(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookUserDao.getBookUsersByType(BookUserType.LOAN)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean {
                // todo add check
                return false
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryLoans(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookUserDao.deleteBookUsersByType(BookUserType.LOAN)

                if ((result.numResults ?: 0) <= 0 || result.loanList?.isNullOrEmpty() == true) return
                for (bookUser in result.loanList) bookUser.type = BookUserType.LOAN
                bookUserDao.insertBookUsers(result.loanList)
            }

        }.build()
    }

    suspend fun getLibraryReservations(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookUserDao.getBookUsersByType(BookUserType.RESERVATION)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean {
                // todo add check
                return false
            }

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryReservations(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookUserDao.deleteBookUsersByType(BookUserType.RESERVATION)

                if ((result.numResults ?: 0) <= 0 || result.reservationList?.isNullOrEmpty() == true) return
                for (bookUser in result.reservationList) bookUser.type = BookUserType.RESERVATION
                bookUserDao.insertBookUsers(result.reservationList)
            }

        }.build()
    }

    suspend fun getLibraryHistory(): LiveData<Resource<List<BookUser>>> {
        return object : NetworkBoundResource<List<BookUser>, NetworkResponseBookUser>() {
            override suspend fun loadFromDb(): LiveData<List<BookUser>> {
                return bookUserDao.getBookUsersByType(BookUserType.HISTORY)
            }

            override suspend fun shouldFetch(data: List<BookUser>?): Boolean = false

            override suspend fun createCall(): LiveData<ApiResponse<NetworkResponseBookUser>> {
                val wsUserIdToken: Token? = tokenDao.getTokenById(Const.TABLE_TOKEN_VALUE_ID_WS_USER_ID)
                return libraryNetworkService.fetchLibraryHistory(token=wsUserIdToken?.token ?: "")
            }

            override suspend fun saveCallResult(result: NetworkResponseBookUser) {
                bookUserDao.deleteBookUsersByType(BookUserType.HISTORY)

                if ((result.numResults ?: 0) <= 0 || result.historyList?.isNullOrEmpty() == true) return
                for (bookUser in result.historyList) bookUser.type = BookUserType.HISTORY
                bookUserDao.insertBookUsers(result.historyList)
            }

        }.build()
    }

}