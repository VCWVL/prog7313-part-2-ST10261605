package com.example.prog7313_part2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CategoryReportActivity : AppCompatActivity() {

    private lateinit var btnStartPeriod: Button
    private lateinit var btnEndPeriod: Button
    private lateinit var btnSearchCategory: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var listOfCategoryDisplay: TextView

    private var startDate: String? = null
    private var endDate: String? = null

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_report)

        btnStartPeriod = findViewById(R.id.btnStartPeriod)
        btnEndPeriod = findViewById(R.id.btnEndPeriod)
        btnSearchCategory = findViewById(R.id.btnSearchCategory)
        recyclerView = findViewById(R.id.recyclerView)
        listOfCategoryDisplay = findViewById(R.id.listOfCategoryDisplay)

        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(emptyList())
        recyclerView.adapter = categoryAdapter

        btnStartPeriod.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                btnStartPeriod.text = "Start: $selectedDate"
            }
        }

        btnEndPeriod.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                btnEndPeriod.text = "End: $selectedDate"
            }
        }

        btnSearchCategory.setOnClickListener {
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Please select start and end periods", Toast.LENGTH_SHORT).show()
            } else {
                loadCategoryData(startDate!!, endDate!!)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val formattedDate = dateFormatter.format(selectedCalendar.time)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun loadCategoryData(startDateStr: String, endDateStr: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val startDate = dateFormatter.parse(startDateStr)!!
        val endDate = dateFormatter.parse(endDateStr)!!

        val startTimestamp = com.google.firebase.Timestamp(startDate)
        val endTimestamp = com.google.firebase.Timestamp(endDate)

        db.collection("expenses")
            .whereEqualTo("userID", currentUserId)
            .whereGreaterThanOrEqualTo("date", startTimestamp)
            .whereLessThanOrEqualTo("date", endTimestamp)
            .get()
            .addOnSuccessListener { result ->
                val categoryAmountMap = mutableMapOf<String, Double>()

                for (doc in result) {
                    val expense = doc.toObject(Expense::class.java)

                    val category = expense.category ?: "Unknown"
                    val amount = expense.amount ?: 0.0

                    categoryAmountMap[category] = categoryAmountMap.getOrDefault(category, 0.0) + amount
                }

                val categoryAmountList = categoryAmountMap.map { CategoryAmount(it.key, it.value) }

                listOfCategoryDisplay.text = "List of category amounts for period: $startDateStr to $endDateStr"

                if (categoryAmountList.isEmpty()) {
                    Toast.makeText(this, "No data for this selected time period", Toast.LENGTH_SHORT).show()
                }

                categoryAdapter.updateData(categoryAmountList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
