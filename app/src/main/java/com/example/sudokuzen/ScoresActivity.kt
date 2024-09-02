package com.example.sudokuzen
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        // Find the Back button by its ID
        val backButton = findViewById<Button>(R.id.backButton)

        // Set a click listener on the Back button
        backButton.setOnClickListener {
            // Create an Intent to navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
