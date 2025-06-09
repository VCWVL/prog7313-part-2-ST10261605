package com.example.prog7313_part2

import com.google.firebase.Timestamp

data class Expense(
    val id: String = "", // Firestore document ID
    val userID: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val date: Timestamp? = null,
    val description: String = "",
    val startDate: String? = "",
    val endDate: String? = "",
    val fileUri: String? = null
)
