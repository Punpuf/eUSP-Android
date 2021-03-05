package com.punpuf.e_usp.db

import androidx.room.*
import com.punpuf.e_usp.Const.Companion.TABLE_TOKEN_NAME
import com.punpuf.e_usp.vo.Token

@Dao
interface TokenDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: Token)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokenList(tokenList: List<Token>)

    @Query ("SELECT * FROM $TABLE_TOKEN_NAME WHERE id = :id LIMIT 1")
    suspend fun getTokenById(id: Int): Token?

    @Delete
    suspend fun deleteToken(token: Token)

    @Query("DELETE FROM $TABLE_TOKEN_NAME")
    suspend fun deleteAllTokens()
}