package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.punpuf.e_usp.Const.Companion.TABLE_USER_PROFILE_NAME
import com.punpuf.e_usp.vo.UserProfile

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfiles(userProfiles: List<UserProfile>)

    @Query("DELETE FROM $TABLE_USER_PROFILE_NAME")
    suspend fun deleteAllUserProfiles()

    @Query("SELECT * FROM $TABLE_USER_PROFILE_NAME")
    fun getAllUserProfilesData(): LiveData<List<UserProfile>>

    @Query("SELECT * FROM $TABLE_USER_PROFILE_NAME")
    suspend fun getAllUserProfiles(): List<UserProfile>
}