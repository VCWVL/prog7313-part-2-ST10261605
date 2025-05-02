package com.example.prog7313_part2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Income::class], version = 2) //updating version as migrations are made
abstract class IncomeDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: IncomeDatabase? = null

        fun getDatabase(context: Context): IncomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IncomeDatabase::class.java,
                    "income_database"
                )
                    .fallbackToDestructiveMigration() //enabling this because making changes to income data class
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
