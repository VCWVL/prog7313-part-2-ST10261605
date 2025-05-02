package com.example.prog7313_part2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {

    @Insert
    fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense_table")
    fun getAllExpense(): List<Expense>
}
