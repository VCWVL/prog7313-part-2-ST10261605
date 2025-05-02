package com.example.prog7313_part2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class register : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val executorService = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //initializing DB
        db = AppDatabase.getDatabase(this)

        //fetching input values
        val nameField = findViewById<EditText>(R.id.name)
        val surnameField = findViewById<EditText>(R.id.surname)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.button2)
        val loginLink = findViewById<TextView>(R.id.LoginLink)

        loginLink.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val surname = surnameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            //if any of the fields are empty, display toast message letting user know
            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else { //if not, creating a new user
                val newUser = User(
                    firstName = name,
                    surname = surname,
                    email = email,
                    password = password
                )

                executorService.execute {
                    db.userDao().insertUser(newUser)

                    //ensuring it is not running on main thread, displaying toast if register successful then taking user to login page
                    runOnUiThread {
                        Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
