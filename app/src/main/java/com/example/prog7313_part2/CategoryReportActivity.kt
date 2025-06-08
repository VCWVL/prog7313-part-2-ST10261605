package com.example.prog7313_part2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CategoryReportActivity : AppCompatActivity() {

    private lateinit var edtMonthPicker: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_report)

        edtMonthPicker = findViewById(R.id.edtMonthPicker)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(emptyList())
        recyclerView.adapter = categoryAdapter

        edtMonthPicker.setOnClickListener {
            showMonthPickerDialog()
        }
    }

    private fun showMonthPickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, _ ->

                val monthShortName = getMonthShortName(month) // Correct 3-letter month name
                val selectedYear = year.toString()

                // For display in EditText → can show as 06/2025
                val selectedMonthDisplay = String.format("%02d", month + 1)
                edtMonthPicker.setText("$selectedMonthDisplay/$selectedYear")

                // Correct pattern for LIKE query:
                val monthYearPattern = "$monthShortName%, $selectedYear%"

                // Load data
                loadCategoryData(monthYearPattern)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    // Helper function to map month number → "Jan", "Feb", etc.
    private fun getMonthShortName(month: Int): String {
        val monthNames = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return monthNames[month]
    }

    private fun loadCategoryData(monthPattern: String) {
        val expenseDao = ExpenseDatabase.getDatabase(this).expenseDao()

        // Read logged in user id from SharedPreferences
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPref.getInt("logged_in_user_id", -1)  // default -1 means no user saved

        if (userId == -1) {
            // No logged in user found, handle error or redirect to login
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val categoryAmountList = expenseDao.getCategoryAmountForMonth(userId.toString(), monthPattern)

            withContext(Dispatchers.Main) {
                // If no data, you can show a toast (optional)
                if (categoryAmountList.isEmpty()) {
                    Toast.makeText(this@CategoryReportActivity, "No data for this selected time period", Toast.LENGTH_SHORT).show()
                }
                categoryAdapter.updateData(categoryAmountList)
            }
        }
    }
}
