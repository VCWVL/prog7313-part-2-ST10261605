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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class AddExpenseFragment : Fragment() {

    //declaring variables
    private lateinit var categorySpinner: Spinner
    private lateinit var edtAmount: EditText
    private lateinit var edtDatePicker: EditText //date of expense
    private lateinit var edtStartDate: EditText //start date
    private lateinit var edtEndDate: EditText //end date
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private var userId: Int = -1 //making userId a global variable so that expense object can be created with it
    private lateinit var btnUploadReceipt: Button
    private var selectedFileUri: Uri? = null
    private val FILE_PICKER_REQUEST_CODE = 100
    private lateinit var txtFileName: TextView

    //variables for firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        //initializing firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        //setting variable values to user input
        categorySpinner = view.findViewById(R.id.categorySelection)
        edtAmount = view.findViewById(R.id.edtAmount)
        edtDatePicker = view.findViewById(R.id.edtDatePicker)
        edtDescription = view.findViewById(R.id.edtDescription)
        edtStartDate = view.findViewById(R.id.edtDatePicker2)
        edtEndDate = view.findViewById(R.id.edtDatePicker3)
        btnSave = view.findViewById(R.id.btnSaveExpense)
        btnUploadReceipt = view.findViewById(R.id.btnUploadReceipt)
        txtFileName = view.findViewById(R.id.txtFileName)


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
        //when the user uploads an image
        btnUploadReceipt = view.findViewById(R.id.btnUploadReceipt)

        btnUploadReceipt.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        //upon the user clicking the save button, save their input into expense database using function
        btnSave.setOnClickListener {
            saveExpenseData()
        }
        return view
    }

    //function for saving user input into database
    private fun saveExpenseData() {
        val category = categorySpinner.selectedItem?.toString() ?: "Other"
        val amountText = edtAmount.text.toString()
        val date = edtDatePicker.text.toString()
        val description = edtDescription.text.toString()
        val startDate = edtStartDate.text.toString()
        val endDate = edtEndDate.text.toString()

        //Firebase authentication check
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid
        if (userUid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountText.isEmpty() || date.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || currentUser == null) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(requireContext(), "Invalid amount.", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val expenseId = firestore.collection("expenses").document().id

        if (selectedFileUri != null) {

            //upload receipt first, then save data
            val storageRef = storage.reference.child("expense_receipts/${UUID.randomUUID()}")
            storageRef.putFile(selectedFileUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    val expense = Expense(
                        id = expenseId,
                        userID = userUid,
                        category = category,
                        amount = amount,
                        date = date,
                        description = description,
                        startDate = startDate,
                        endDate = endDate,
                        fileUri = uri.toString()
                    )
                    saveToFirestore(firestore, expense, expenseId)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to upload receipt.", Toast.LENGTH_SHORT).show()
                }
        } else {
            //no receipt, save directly
            val expense = Expense(
                id = expenseId,
                userID = userUid,
                category = category,
                amount = amount,
                date = date,
                description = description,
                startDate = startDate,
                endDate = endDate,
                fileUri = null
            )
            saveToFirestore(firestore, expense, expenseId)
        }
    }

    //function for saving expense to firestore
    private fun saveToFirestore(
        firestore: FirebaseFirestore,
        expense: Expense,
        expenseId: String
    ) {
        firestore.collection("expenses").document(expenseId)
            .set(expense)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense saved successfully.", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save expense: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    //function to clear all fields
    private fun clearFields() {
        edtAmount.text.clear()
        edtDatePicker.text.clear()
        edtDescription.text.clear()
        edtStartDate.text.clear()
        edtEndDate.text.clear()
        selectedFileUri = null
        txtFileName.text = ""
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

                Toast.makeText(
                    requireContext(),
                    "File selected: ${uri.lastPathSegment}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
