package com.punpuf.e_bandejao.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_bandejao.Const.Companion.TABLE_PROFILE_PIC_NAME
import com.punpuf.e_bandejao.vo.ProfilePictureInfo

@Dao
interface ProfilePictureInfoDao {

    @Query("DELETE FROM $TABLE_PROFILE_PIC_NAME")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfilePictureLocation(profilePictureInfo: ProfilePictureInfo)

    @Query("SELECT * FROM $TABLE_PROFILE_PIC_NAME WHERE id = :id LIMIT 1")
    fun geProfileLocationPictureById(id: Int): LiveData<ProfilePictureInfo?>

}