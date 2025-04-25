package com.example.prog7313_part2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class AddMoney : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_money)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //functionality for user to be able to choose a date for Add Money feature
//
//        //getting current date and time here (so when calendar pops up , current date & time is default)
//        val calendar = Calendar.getInstance()
//
//        //fetching the chosen date from the edit text
//        val chosenDate = findViewById<EditText>(R.id.edtDatePicker)
//
//        //creating pop up that lets users scroll through calendar
//        val datePicker = DatePickerDialog(
//            this, //"this" means we are using this constructor in this screen
//            { view, year, month, dayOfMonth -> //the values the user selects
//                val selectedDate = "$dayOfMonth/${month + 1}/$year" //constructing a string w those values
//                chosenDate.setText(selectedDate) //setting editText text to that string
//            },
//            calendar.get(Calendar.YEAR), //fetching current year, month and day for default
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        )
//
//        //if user clicks EditText then calendar will pop up
//        chosenDate.setOnClickListener {
//            datePicker.show()
//        }

        val chosenDate = findViewById<EditText>(R.id.edtDatePicker)

        // Build the date picker
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .build()

        // Show the date picker when the EditText is clicked
        chosenDate.setOnClickListener {
            datePicker.show(supportFragmentManager, "date_picker")
        }

        // When the user selects a date
        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = datePicker.headerText // example: "Apr 20, 2025"
            chosenDate.setText(selectedDate)
        }

    }
}