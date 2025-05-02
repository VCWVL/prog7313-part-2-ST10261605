package com.example.prog7313_part2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_table")
data class Income(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="incomeID")
    val id: Int = 0,
    @ColumnInfo(name = "userID")
    val userID: String, //associate each income with a user via userID
    @ColumnInfo(name="category")
    val category: String,
    @ColumnInfo(name="incomeAmount")
    val amount: Double,
    @ColumnInfo(name="date")
    val date: String,
    @ColumnInfo(name="description")
    val description: String
)
