package com.example.prog7313_part2

data class Budget(
    val id: Int = 0,
    val userID: String,
    val budgetPeriod: String,
    val totalAmount: Double,
    val category: String,
    val minGoal: Double,
    val maxGoal: Double,
    val description: String
)


