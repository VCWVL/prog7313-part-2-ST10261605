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

class BudgetFragment : Fragment() {

    //declaring variables
    private lateinit var db: BudgetDatabase
    private lateinit var budgetPeriod: EditText
    private lateinit var edtTotalAmount: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var edtMinAmount: EditText
    private lateinit var edtMaxAmount: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1 //making userId a global variable so that budget object can be created with it

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        //fetching budget database
        db = BudgetDatabase.getDatabase(requireContext())

        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("logged_in_user_id", -1)

        //setting variable values to user input

        budgetPeriod = view.findViewById(R.id.edtMonthPicker)
        edtTotalAmount = view.findViewById(R.id.edtTotalMonthlyGoal)
        categorySpinner = view.findViewById(R.id.categorySelection)
        edtMinAmount = view.findViewById(R.id.edtTotalMonthlyGoal2)
        edtMaxAmount = view.findViewById(R.id.edtTotalMonthlyGoal3)
        edtDescription = view.findViewById(R.id.edtDescription)
        btnSave = view.findViewById(R.id.btnSaveMoney)

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


        //show the date picker when the EditText is clicked
        budgetPeriod.setOnClickListener {
            datePicker.show(parentFragmentManager, "month_picker")
        }

        //when the user selects a date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker.headerText
            budgetPeriod.setText(selectedDate)
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

        val timePeriod = budgetPeriod.text.toString()
        val amountText = edtTotalAmount.text.toString()
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val minimumGoal = edtMinAmount.text.toString()
        val maximumGoal = edtMaxAmount.text.toString()
        val description = edtDescription.text.toString()

        //if the time period input field is not empty then
        if (timePeriod.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull() //convert input to double
            val minAmount = minimumGoal.toDouble()
            val maxAmount = maximumGoal.toDouble()
            if (amount != null) { //if amount is not null then create new budget entry
                val budget = Budget(
                    userID = userId.toString(),
                    budgetPeriod = timePeriod,
                    totalAmount = amount,
                    category = category,
                    minGoal = minAmount,
                    maxGoal = maxAmount,
                    description = description
                )

                //insert new budget entry into database
                lifecycleScope.launch(Dispatchers.IO) {
                    db.budgetDao().insertBudget(budget)
                }

                //display toast message to let user know it was saved successfully
                Toast.makeText(requireContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show()

                //clearing input fields
                budgetPeriod.text.clear()
                edtTotalAmount.text.clear()
                edtMinAmount.text.clear()
                edtMaxAmount.text.clear()
                edtDescription.text.clear()

            } else { //if amount is null, display toast message to let user know
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else { //if budget period field is empty, display toast message to let user know
            Toast.makeText(requireContext(), "budget period is required", Toast.LENGTH_SHORT).show()
        }
    }
}
