// ScoresActivity.kt
package com.example.sudokuzen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        // Find the TextView for total score
        val totalScoreTextView = findViewById<TextView>(R.id.totalScoreTextView)

        // Retrieve the total score from SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val totalScore = sharedPreferences.getInt("score", 0)

        // Display the total score
        totalScoreTextView.text = "Total Score: $totalScore"

        // Find the Back button
        val backButton = findViewById<Button>(R.id.backButton)

        // Set click listener on Back button
        backButton.setOnClickListener {
            // Navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
