package com.example.prog7313_part2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val repository: ExpenseRepository
//
//    init {
//        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
//        repository = ExpenseRepository(expenseDao)
//    }
//
//    //insert new expense (runs in background)
//    fun insertExpense(expense: Expense) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.insertExpense(expense)
//        }
//    }
//
//    //get all expenses
//    fun getAllExpenses(): List<Expense> {
//        return repository.getAllExpenses()
//    }
//
//    //get expenses for a specific user
//    fun getExpensesForUser(userId: String): List<Expense> {
//        return repository.getExpenseForUser(userId)
//    }
//
//    //get expenses between two dates (LiveData)
//    fun getExpensesBetweenDates(startDate: String, endDate: String): LiveData<List<Expense>> {
//        return repository.getExpensesBetweenDates(startDate, endDate)
//    }
//
//    //get category totals for a month
//    fun getCategoryAmountForMonth(userId: Int, monthPattern: String): List<CategoryAmount> {
//        return repository.getCategoryAmountForMonth(userId, monthPattern)
//    }
}
