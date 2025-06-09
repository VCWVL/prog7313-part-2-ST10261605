package com.example.prog7313_part2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313_part2.Expense
import com.example.prog7313_part2.ExpenseAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ExpensesReportActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private val expensesList = mutableListOf<Expense>()

    private lateinit var startButton: Button
    private lateinit var endButton: Button
    private lateinit var searchButton: Button
    private lateinit var displayText: TextView

    private var startDate: String? = null
    private var endDate: String? = null

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_report)

        recyclerView = findViewById(R.id.recyclerView2)
        startButton = findViewById(R.id.btnStartPeriod)
        endButton = findViewById(R.id.btnEndPeriod)
        searchButton = findViewById(R.id.btnSearchExpenses)
        displayText = findViewById(R.id.listOfExpensesDisplay)

        adapter = ExpenseAdapter(expensesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        startButton.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                startButton.text = "Start: $date"
            }
        }

        endButton.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                endButton.text = "End: $date"
            }
        }

        searchButton.setOnClickListener {
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            } else {
                displayText.text = "List of expenses for: $startDate to $endDate"
                filterExpensesByDateRange(startDate!!, endDate!!)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, y, m, d ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(y, m, d)
            val formatted = formatter.format(selectedCalendar.time)
            onDateSelected(formatted)
        }, year, month, day)

        dpd.show()
    }

    private fun filterExpensesByDateRange(start: String, end: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("expenses")
            .whereEqualTo("userID", currentUserId)
            .whereGreaterThanOrEqualTo("date", start)
            .whereLessThanOrEqualTo("date", end)
            .get()
            .addOnSuccessListener { result ->
                expensesList.clear()
                for (doc in result) {
                    val expense = doc.toObject(Expense::class.java)
                    expensesList.add(expense)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching expenses: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
