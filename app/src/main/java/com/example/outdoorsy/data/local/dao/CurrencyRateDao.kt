package com.example.outdoorsy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.outdoorsy.data.local.entity.CurrencyRateEntity

@Dao
interface CurrencyRateDao {
    @Query(
        "SELECT * FROM currency_rates WHERE baseCurrency = :baseCurrency AND targetCurrency = :targetCurrency LIMIT 1"
    )
    suspend fun getRate(baseCurrency: String, targetCurrency: String): CurrencyRateEntity?

    @Query("SELECT * FROM currency_rates ORDER BY timestamp DESC LIMIT 1")
    suspend fun getMostRecentRate(): CurrencyRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<CurrencyRateEntity>)

    @Query("DELETE FROM currency_rates")
    suspend fun clearAll()

    // This makes the clear/insert operation atomic and safer
    @Transaction
    suspend fun clearAndInsert(rates: List<CurrencyRateEntity>) {
        clearAll()
        insertAll(rates)
    }
}
