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

                val monthNames = arrayOf(
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
                )

                val selectedMonthName = monthNames[month]
                val selectedYear = year.toString()

                // Update EditText
                edtMonthPicker.setText("$selectedMonthName $selectedYear")

                // Now load data for this month
                val monthYearPattern = "$selectedMonthName%, $selectedYear%"

                loadCategoryData(monthYearPattern)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
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
            val categoryAmountList = expenseDao.getCategoryAmountForMonth(userId, monthPattern)

            withContext(Dispatchers.Main) {
                categoryAdapter.updateData(categoryAmountList)

                //Show toast if no result
                if (categoryAmountList.isEmpty()) {
                    Toast.makeText(
                        this@CategoryReportActivity,
                        "No data for selected month",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
    }
}