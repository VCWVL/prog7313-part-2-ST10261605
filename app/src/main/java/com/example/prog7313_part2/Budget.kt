package com.example.prog7313_part2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_table")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="budgetID")
    val id: Int = 0,
    @ColumnInfo(name = "userID")
    val userID: String, //associate each budget with a user via userID
    @ColumnInfo(name="budgetPeriod")
    val budgetPeriod: String,
    @ColumnInfo(name="totalMonthlyAmount")
    val totalAmount: Double,
    @ColumnInfo(name="category")
    val category: String,
    @ColumnInfo(name="minMonthlyGoal")
    val minGoal: Double,
    @ColumnInfo(name="maxMonthlyGoal")
    val maxGoal: Double,
    @ColumnInfo(name="description")
    val description: String
)
