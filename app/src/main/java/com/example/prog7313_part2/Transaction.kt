package com.example.prog7313_part2

import java.util.Date

data class Transaction(
    val id: String,
    val amount: Double,
    val date: Date,
    val category: String,
    val type: TransactionType

)

enum class  TransactionType{
    INCOME,EXPENSE
}