package com.himanshuyadav.questionadder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.himanshuyadav.questionadder.network.RetrofitInstance
import com.himanshuyadav.questionadder.network.Source
import com.himanshuyadav.questionadder.network.Subject
import com.himanshuyadav.questionadder.network.Topic
import kotlinx.coroutines.launch

class EditorActivity : AppCompatActivity() {

    private var authToken: String? = null
    private lateinit var subjectAutoComplete: AutoCompleteTextView
    private lateinit var topicAutoComplete: AutoCompleteTextView
    private lateinit var sourceAutoComplete: AutoCompleteTextView

    // Lists to hold all the data fetched from the API
    private var allSubjects: List<Subject> = emptyList()
    private var allTopics: List<Topic> = emptyList()
    private var allSources: List<Source> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Retrieve the token passed from MainActivity
        authToken = intent.getStringExtra("AUTH_TOKEN")
        if (authToken == null) {
            Toast.makeText(this, "Authentication error. Please restart the app.", Toast.LENGTH_LONG).show()
            finish() // Close the activity if no token is found
            return
        }

        // Get references to our dropdown views
        subjectAutoComplete = findViewById(R.id.subjectAutoComplete)
        topicAutoComplete = findViewById(R.id.topicAutoComplete)
        sourceAutoComplete = findViewById(R.id.sourceAutoComplete)

        // Start fetching the data
        fetchDataForDropdowns()
    }

    private fun fetchDataForDropdowns() {
        // Use the authentication token, ensuring it's not null
        val token = "Bearer $authToken"

        lifecycleScope.launch {
            try {
                // Fetch all data in parallel
                val subjectsResponse = RetrofitInstance.api.getSubjects(token)
                val topicsResponse = RetrofitInstance.api.getTopics(token)
                val sourcesResponse = RetrofitInstance.api.getSources(token)

                // Check if all requests were successful
                if (subjectsResponse.isSuccessful && topicsResponse.isSuccessful && sourcesResponse.isSuccessful) {
                    allSubjects = subjectsResponse.body() ?: emptyList()
                    allTopics = topicsResponse.body() ?: emptyList()
                    allSources = sourcesResponse.body() ?: emptyList()

                    // Now that we have the data, set up the dropdowns
                    setupSubjectDropdown()

                } else {
                    Toast.makeText(this@EditorActivity, "Failed to load initial data.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@EditorActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSubjectDropdown() {
        // Extract just the names for the adapter
        val subjectNames = allSubjects.map { it.subject_name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjectNames)
        subjectAutoComplete.setAdapter(adapter)

        // Set a listener to react when a subject is chosen
        subjectAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedSubjectName = parent.getItemAtPosition(position) as String
            val selectedSubject = allSubjects.find { it.subject_name == selectedSubjectName }

            if (selectedSubject != null) {
                // Filter topics and sources based on the selected subject's ID
                val filteredTopics = allTopics.filter { it.subject_id == selectedSubject.subject_id }
                val filteredSources = allSources.filter { it.subject_id == selectedSubject.subject_id }

                // Update the Topic dropdown
                val topicNames = filteredTopics.map { it.topic_name }
                val topicAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, topicNames)
                topicAutoComplete.setAdapter(topicAdapter)
                topicAutoComplete.setText("", false) // Clear previous selection

                // Update the Source dropdown
                val sourceNames = filteredSources.map { it.source_name }
                val sourceAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sourceNames)
                sourceAutoComplete.setAdapter(sourceAdapter)
                sourceAutoComplete.setText("", false) // Clear previous selection
            }
        }
    }
}