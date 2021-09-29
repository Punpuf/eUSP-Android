package com.punpuf.e_usp.db

import androidx.paging.PagingSource
import androidx.room.*
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_AUTHORS
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_TITLE
import com.punpuf.e_usp.vo.BookOfSearch

@Dao
interface BookOfSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<BookOfSearch>)
    
    @Query("SELECT * FROM $TABLE_BOOK_OF_SEARCH WHERE $TABLE_BOOK_OF_SEARCH_TITLE LIKE '%'||:query||'%' OR $TABLE_BOOK_OF_SEARCH_AUTHORS LIKE '%'||:query||'%'")
    fun pagingSource(query: String): PagingSource<Int, BookOfSearch>

    @Query("DELETE FROM $TABLE_BOOK_OF_SEARCH WHERE $TABLE_BOOK_OF_SEARCH_TITLE LIKE '%'||:query||'%' OR $TABLE_BOOK_OF_SEARCH_AUTHORS LIKE '%'||:query||'%'")
    suspend fun clearQuery(query: String)

    @Query("DELETE FROM $TABLE_BOOK_OF_SEARCH")
    suspend fun clearAll()

}