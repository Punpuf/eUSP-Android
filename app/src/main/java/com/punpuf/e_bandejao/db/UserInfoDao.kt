package com.punpuf.e_bandejao.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_bandejao.Const.Companion.TABLE_USER_INFO_NAME
import com.punpuf.e_bandejao.vo.UserInfo

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInfo)

    @Query("DELETE FROM $TABLE_USER_INFO_NAME")
    suspend fun deleteAllUserProfiles()

    @Query("SELECT * FROM $TABLE_USER_INFO_NAME LIMIT 1")
    fun getUserInfoData(): LiveData<UserInfo?>

    @Query("SELECT * FROM $TABLE_USER_INFO_NAME LIMIT 1")
    fun getUserInfo(): UserInfo?
}