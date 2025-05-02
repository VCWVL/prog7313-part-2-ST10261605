package com.example.prog7313_part2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp

class AddExpenseFragment : Fragment() {

    //declaring variables
    private lateinit var db: ExpenseDatabase
    private lateinit var categorySpinner: Spinner
    private lateinit var edtAmount: EditText
    private lateinit var edtDatePicker: EditText //date of expense
    private lateinit var edtStartDate: EditText //start date
    private lateinit var edtEndDate: EditText //end date
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1 //making userId a global variable so that expense object can be created with it

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        //fetching income database
        db = ExpenseDatabase.getDatabase(requireContext())

        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("logged_in_user_id", -1)

        //setting variable values to user input
        categorySpinner = view.findViewById(R.id.categorySelection)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtDatePicker = view.findViewById(R.id.edtDatePicker)
        edtDescription = view.findViewById(R.id.edtDescription)
        edtStartDate = view.findViewById(R.id.edtDatePicker2)
        edtEndDate = view.findViewById(R.id.edtDatePicker3)
        btnSave = view.findViewById(R.id.btnSaveExpense)

        //adding the categories to the spinner (for the user to choose from)
        val categories = listOf("Shopping", "Home", "Education", "Clothing", "Social", "Car", "Food", "Beauty", "Sports", "Transport", "Other")

        //using an array adapter to store the categories and then display them as a drop down
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        //building the date picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .build()

        //building the date picker
        val datePicker2 = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .build()

        //building the date picker
        val datePicker3 = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .build()

        //show the date picker when the EditText is clicked
        edtDatePicker.setOnClickListener {
            datePicker.show(parentFragmentManager, "date_picker")
        }

        edtStartDate.setOnClickListener {
            datePicker2.show(parentFragmentManager, "date_picker")
        }

        edtEndDate.setOnClickListener {
            datePicker3.show(parentFragmentManager, "date_picker")
        }

        //when the user selects a date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker.headerText //example: Apr 20, 2025
            edtDatePicker.setText(selectedDate)
        }

        datePicker2.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker2.headerText //example: Apr 20, 2025
            edtStartDate.setText(selectedDate)
        }

        datePicker3.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker3.headerText //example: Apr 20, 2025
            edtEndDate.setText(selectedDate)
        }

        //upon the user clicking the save button, save their input into expense database using function
        btnSave.setOnClickListener {
            saveExpenseData()
        }
        return view
    }

    //function for saving user input into database
    private fun saveExpenseData() {
        //declaring variables
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val amountText = edtAmount.text.toString()
        val date = edtDatePicker.text.toString()
        val description = edtDescription.text.toString()
        val startDate = edtStartDate.text.toString()
        val endDate = edtEndDate.text.toString()

        //if the amount text input field is not empty then
        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull() //convert input to double
            if (amount != null) { //if amount is not null then create new income entry
                val expense = Expense(
                    userID = userId.toString(),
                    category = category,
                    amount = amount,
                    date = date,
                    description = description,
                    startDate = startDate,
                    endDate = endDate
                )

                //insert new expense entry into database
                lifecycleScope.launch(Dispatchers.IO) {
                    db.expenseDao().insertExpense(expense)
                }

                //display toast message to let user know it was saved successfully
                Toast.makeText(requireContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show()

                //clearing input fields
                edtAmount.text.clear()
                edtDatePicker.text.clear()
                edtDescription.text.clear()
                edtStartDate.text.clear()
                edtEndDate.text.clear()

            } else { //if amount is null, display toast message to let user know
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else { //if amount field is empty, display toast message to let user know
            Toast.makeText(requireContext(), "Amount is required", Toast.LENGTH_SHORT).show()
        }
    }
}
