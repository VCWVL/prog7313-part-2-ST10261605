package com.example.prog7313_part2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncomeDao {

    @Insert
     fun insertIncome(income: Income)

    @Query("SELECT * FROM income_table")
    fun getAllIncome(): List<Income>

    @Query("SELECT * FROM income_table WHERE userID = :userId")
    fun getIncomeForUser(userId: String): List<Income>
}
