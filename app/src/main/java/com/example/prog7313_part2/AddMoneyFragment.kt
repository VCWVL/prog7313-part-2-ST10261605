package com.example.prog7313_part2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddMoneyFragment : Fragment() {

    //declaring variables
    private lateinit var categorySpinner: Spinner
    private lateinit var edtAmount: EditText
    private lateinit var edtDatePicker: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1 //making userId a global variable so that income object can be created with it
    private lateinit var btnChooseFile: Button
    private var selectedFileUri: Uri? = null
    private val FILE_PICKER_REQUEST_CODE = 100
    private lateinit var txtFileName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_money, container, false)

        //setting variable values to user input
        categorySpinner = view.findViewById(R.id.categorySelection)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtDatePicker = view.findViewById(R.id.edtDatePicker)
        edtDescription = view.findViewById(R.id.edtDescription)
        btnSave = view.findViewById(R.id.btnSaveMoney)
        txtFileName = view.findViewById(R.id.txtFileName)


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
        //when the user uploads an image
        btnChooseFile = view.findViewById(R.id.btnChooseFile)

        btnChooseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        //upon the user clicking the save button, save their input into income database using function
        btnSave.setOnClickListener {
            saveIncomeData()
        }
        return view
    }

    //function for saving user input into database
    private fun saveIncomeData() {
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val amountText = edtAmount.text.toString()
        val date = edtDatePicker.text.toString()
        val description = edtDescription.text.toString()

        //Firebase authentication check
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        if (userUid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                val income = Income(
                    userID = userUid, //Firebase UID
                    category = category,
                    amount = amount,
                    date = date,
                    description = description,
                    fileUri = selectedFileUri?.toString()
                )

                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val incomeId = db.collection("incomes").document().id
                db.collection("incomes").document(incomeId).set(income)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Income saved successfully", Toast.LENGTH_SHORT).show()
                        edtAmount.text.clear()
                        edtDatePicker.text.clear()
                        edtDescription.text.clear()
                        txtFileName.text = ""
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error saving income: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Amount is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return uri.lastPathSegment
    }

    //Handling choose file function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedFileUri = uri

                val fileName = getFileNameFromUri(uri)
                txtFileName.text = fileName ?: "File selected"

                Toast.makeText(requireContext(), "File selected: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}