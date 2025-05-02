package com.example.prog7313_part2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="userID")
    val id: Int=0,
    @ColumnInfo(name = "firstName")
    val firstName: String,
    @ColumnInfo(name = "surname")
    val surname: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String
)

