package com.example.prog7313_part2

import com.google.firebase.Timestamp

data class Income(
    val userID: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val date: Timestamp? = null,
    val description: String = "",
    val fileUri: String? = null
)
