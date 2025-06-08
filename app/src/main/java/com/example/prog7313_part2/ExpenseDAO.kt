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

    @Query("SELECT * FROM expense_table WHERE userID = :userId")
    fun getExpenseForUser(userId: String): List<Expense>

    @Query("""
    SELECT category, SUM(expenseAmount) AS totalAmount 
    FROM expense_table 
    WHERE userID = :userId AND date LIKE :monthPattern
    GROUP BY category
""")

    fun getCategoryAmountForMonth(userId: String, monthPattern: String): List<CategoryAmount>
}
