package com.example.prog7313_part2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): List<User>

    //for the login
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    fun login(email: String, password: String): User?

    @Delete
    fun delete(user: User)


}