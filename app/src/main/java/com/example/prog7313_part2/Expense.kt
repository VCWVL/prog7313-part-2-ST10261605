package com.example.prog7313_part2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="expenseID")
    val id: Int = 0,
    @ColumnInfo(name = "userID")
    val userID: String, //associate each expense with a user via userID
    @ColumnInfo(name="category")
    val category: String,
    @ColumnInfo(name="expenseAmount")
    val amount: Double,
    @ColumnInfo(name="date")
    val date: String,
    @ColumnInfo(name="description")
    val description: String,
    @ColumnInfo(name="StartDate")
    val startDate: String,
    @ColumnInfo(name="EndDate")
    val endDate: String
)
