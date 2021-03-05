package com.punpuf.e_usp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.vo.Boleto

@Dao
interface BoletoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoleto(boletoDao: Boleto)

    @Query("SELECT * FROM ${Const.TABLE_BOLETO} LIMIT 1")
    fun getBoleto(): LiveData<Boleto?>

    @Query("DELETE FROM ${Const.TABLE_BOLETO}")
    suspend fun deleteAll()
}