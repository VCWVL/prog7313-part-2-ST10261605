package com.example.prog7313_part2
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//private lateinit var viewModel: ExpenseViewModel
//private lateinit var recyclerView: RecyclerView
//private lateinit var adapter: ExpenseAdapter
//
//private var selectedStartDate: String? = null
//private var selectedEndDate: String? = null
//private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//
//class ExpensesReportActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_expenses_report)
//
//        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
//        recyclerView = findViewById(R.id.recyclerView2)
//        adapter = ExpenseAdapter(emptyList())
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Date picker buttons
//        findViewById<Button>(R.id.btnStartPeriod).setOnClickListener {
//            showDatePicker { date ->
//                selectedStartDate = date
//                Toast.makeText(this, "Start: $date", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        findViewById<Button>(R.id.btnEndPeriod).setOnClickListener {
//            showDatePicker { date ->
//                selectedEndDate = date
//                Toast.makeText(this, "End: $date", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        findViewById<Button>(R.id.btnSearchExpenses).setOnClickListener {
//            if (selectedStartDate != null && selectedEndDate != null) {
//                viewModel.getExpensesBetweenDates(selectedStartDate!!, selectedEndDate!!)
//                    .observe(this) { expenses ->
//                        adapter.updateExpenses(expenses)
//                    }
//            } else {
//                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//
//    private fun showDatePicker(onDateSelected: (String) -> Unit) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        val datePicker = DatePickerDialog(this, { _, y, m, d ->
//            calendar.set(y, m, d)
//            val formattedDate = formatter.format(calendar.time)
//            onDateSelected(formattedDate)
//        }, year, month, day)
//
//        datePicker.show()
//    }
//}
//
//
