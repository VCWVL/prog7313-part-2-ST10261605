package com.example.prog7313_part2

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

class AddMoneyFragment : Fragment() {

    //declaring variables
    private lateinit var db: IncomeDatabase
    private lateinit var categorySpinner: Spinner
    private lateinit var edtAmount: EditText
    private lateinit var edtDatePicker: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_money, container, false)

        //fetching income database
        db = IncomeDatabase.getDatabase(requireContext())

        //setting variable values to user input
        categorySpinner = view.findViewById(R.id.categorySelection)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtDatePicker = view.findViewById(R.id.edtDatePicker)
        edtDescription = view.findViewById(R.id.edtDescription)
        btnSave = view.findViewById(R.id.btnSaveMoney)

        //adding the categories to the spinner (for the user to choose from)
        val categories = listOf("Salary", "Investment", "Allowance", "Part-time", "Bonus", "Other")

        //using an array adapter to store the categories and then display them as a drop down
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        //building the date picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .build()

        //show the date picker when the EditText is clicked
        edtDatePicker.setOnClickListener {
            datePicker.show(parentFragmentManager, "date_picker")
        }

        //when the user selects a date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker.headerText //example: Apr 20, 2025
            edtDatePicker.setText(selectedDate)
        }

        //upon the user clicking the save button, save their input into income database using function
        btnSave.setOnClickListener {
            saveIncomeData()
        }
        return view
    }

    //function for saving user input into database
    private fun saveIncomeData() {
        //declaring variables
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val amountText = edtAmount.text.toString()
        val date = edtDatePicker.text.toString()
        val description = edtDescription.text.toString()

        //if the amount text input field is not empty then
        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull() //convert input to double
            if (amount != null) { //if amount is not null then create new income entry
                val income = Income(
                    category = category,
                    amount = amount,
                    date = date,
                    description = description
                )

                //insert new income entry into database
                lifecycleScope.launch(Dispatchers.IO) {
                    db.incomeDao().insertIncome(income)
                }

                //display toast message to let user know it was saved successfully
                Toast.makeText(requireContext(), "Income saved successfully", Toast.LENGTH_SHORT).show()

                //clearing input fields
                edtAmount.text.clear()
                edtDatePicker.text.clear()
                edtDescription.text.clear()

            } else { //if amount is null, display toast message to let user know
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else { //if amount field is empty, display toast message to let user know
            Toast.makeText(requireContext(), "Amount is required", Toast.LENGTH_SHORT).show()
        }
    }
}
