package com.example.prog7313_part2

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDAO) {

    fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun getAllExpenses(): List<Expense> {
        return expenseDao.getAllExpense()
    }

    fun getExpenseForUser(userId: String): List<Expense> {
        return expenseDao.getExpenseForUser(userId)
    }

    fun getExpensesBetweenDates(startDate: String, endDate: String): LiveData<List<Expense>> {
        return expenseDao.getExpensesBetweenDates(startDate, endDate)
    }


}
