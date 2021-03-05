package com.punpuf.e_bandejao.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.Const.Companion.TABLE_BOOK_USER
import com.punpuf.e_bandejao.Const.Companion.TABLE_BOOK_USER_TYPE
import com.punpuf.e_bandejao.vo.BookUser
import com.punpuf.e_bandejao.vo.BookUserType

@Dao
interface BookUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookUsers(userBooks: List<BookUser>)

    @Query("SELECT * FROM $TABLE_BOOK_USER WHERE $TABLE_BOOK_USER_TYPE = :type")
    fun getBookUsersByType(type: BookUserType): LiveData<List<BookUser>>

    @Query("DELETE FROM $TABLE_BOOK_USER WHERE $TABLE_BOOK_USER_TYPE = :type")
    suspend fun deleteBookUsersByType(type: BookUserType)

    @Query("DELETE FROM $TABLE_BOOK_USER")
    suspend fun deleteAll()

}