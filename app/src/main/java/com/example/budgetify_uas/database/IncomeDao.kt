package com.example.budgetify_uas.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income")
    fun getAllIncome(): List<Income>

    @Insert
    suspend fun insertIncome(income: Income)

    @Query("DELETE FROM income WHERE id = :id")
    fun deleteIncome(id: String)

    @Query("SELECT SUM(amount) FROM income")
    fun getTotalIncome(): Double

    @Query("DELETE FROM income")
    suspend fun nukeTable()
}