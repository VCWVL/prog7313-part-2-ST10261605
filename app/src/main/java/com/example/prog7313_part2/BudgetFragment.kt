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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class BudgetFragment : Fragment() {
    //declaring variables
    private lateinit var budgetPeriod: EditText
    private lateinit var edtTotalAmount: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var edtMinAmount: EditText
    private lateinit var edtMaxAmount: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1 //making userId a global variable so that budget object can be created with it

    //variables for firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        //initializing firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

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
            saveBudgetData()
        }
        return view
    }

    //function for saving user input into database
    private fun saveBudgetData() {
        //declaring variables

        val timePeriod = budgetPeriod.text.toString()
        val amountText = edtTotalAmount.text.toString()
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val minimumGoal = edtMinAmount.text.toString()
        val maximumGoal = edtMaxAmount.text.toString()
        val description = edtDescription.text.toString()

        //Firebase authentication check
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        if (userUid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        //if the time period input field is not empty then
        if (timePeriod.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull() //convert input to double
            val minAmount = minimumGoal.toDouble()
            val maxAmount = maximumGoal.toDouble()

            // Generate document ID
            val budgetId = firestore.collection("budgets").document().id

            if (amount != null) { //if amount is not null then create new budget entry
                val budget = Budget(
                    userID = userUid,
                    budgetPeriod = timePeriod,
                    totalAmount = amount,
                    category = category,
                    minGoal = minAmount,
                    maxGoal = maxAmount,
                    description = description
                )

                firestore.collection("budgets").document(budgetId).set(budget)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Budget saved to Firebase", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save budget", Toast.LENGTH_SHORT).show()
                    }

                //display toast message to let user know it was saved successfully
                Toast.makeText(requireContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show()

            } else { //if amount is null, display toast message to let user know
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else { //if budget period field is empty, display toast message to let user know
            Toast.makeText(requireContext(), "budget period is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        //clearing input fields
        budgetPeriod.text.clear()
        edtTotalAmount.text.clear()
        edtMinAmount.text.clear()
        edtMaxAmount.text.clear()
        edtDescription.text.clear()
    }
}
