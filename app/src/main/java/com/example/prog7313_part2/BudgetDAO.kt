package com.example.prog7313_part2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BudgetDAO {

    @Insert
    fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budget_table")
    fun getAllBudget(): List<Budget>

    @Query("SELECT * FROM budget_table WHERE userID = :userId")
    fun getBudgetForUser(userId: String): List<Budget>
}
