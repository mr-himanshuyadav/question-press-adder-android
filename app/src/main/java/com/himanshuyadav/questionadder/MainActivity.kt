package com.himanshuyadav.questionadder

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.himanshuyadav.questionadder.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the line that loads your XML layout.
        setContentView(R.layout.activity_main)

        // Get references to the UI elements from our XML layout
        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        // Set a listener to execute code when the button is clicked
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Simple validation to make sure fields are not empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Launch a coroutine to perform the network request on a background thread
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.loginUser(username, password)

                    if (response.isSuccessful && response.body() != null) {
                        // API call was successful
                        val loginResponse = response.body()!!
                        val welcomeMessage = "Login Successful! Welcome, ${loginResponse.user_display_name}"
                        Toast.makeText(this@MainActivity, welcomeMessage, Toast.LENGTH_LONG).show()

                        // TODO: Save the token and navigate to the next screen

                    } else {
                        // API call failed (e.g., wrong credentials)
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Toast.makeText(this@MainActivity, "Login Failed: $errorBody", Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    // Handle network errors (e.g., no internet connection)
                    Toast.makeText(this@MainActivity, "Network Error: Please check your connection", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    // Handle other unexpected errors
                    Toast.makeText(this@MainActivity, "An unexpected error occurred: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}