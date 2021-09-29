package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_TYPE
import com.punpuf.e_usp.vo.BookOfUserType
import com.punpuf.e_usp.vo.BookUser

@Dao
interface BookOfUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookUsers(userBooks: List<BookUser>)

    @Query("SELECT * FROM $TABLE_BOOK_OF_USER WHERE $TABLE_BOOK_OF_USER_TYPE = :type")
    fun getBookUsersByType(type: BookOfUserType): LiveData<List<BookUser>>

    @Query("DELETE FROM $TABLE_BOOK_OF_USER WHERE $TABLE_BOOK_OF_USER_TYPE = :type")
    suspend fun deleteBookUsersByType(type: BookOfUserType)

    @Query("DELETE FROM $TABLE_BOOK_OF_USER")
    suspend fun deleteAll()

}