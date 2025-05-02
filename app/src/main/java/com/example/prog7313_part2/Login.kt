package com.example.prog7313_part2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    //declaring variables
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //setting variable values to user input
        emailInput = findViewById(R.id.LoginEmail)
        passwordInput = findViewById(R.id.textInputPassword)
        loginButton = findViewById(R.id.button3)
        registerLink = findViewById(R.id.RegisterLink)

        //when login button is clicked
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim() //fetch email
            val password = passwordInput.text.toString().trim() //fetch password

            //if email & password arent empty then fetch email & password that corresponds in database
            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = withContext(Dispatchers.IO) {
                        AppDatabase.getDatabase(applicationContext).userDao().login(email, password)
                    }

                    //if user exists then display toast message that confirms login then take user to home page
                    if (user != null) {

                        //storing userID in shared preferences so that other fragments/activities can access the userID
                        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                        sharedPref.edit().putInt("logged_in_user_id", user.id).apply()

                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                        //go to home screen
                        val intent = Intent(this@Login, Home::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Login, "Invalid credentials, please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
        }
    }
}
