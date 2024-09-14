// SuccessActivity.kt

package com.example.sudokuzen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sudokuzen.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve difficulty and points from Intent
        val difficulty = intent.getStringExtra("difficulty") ?: "Medium"
        val points = intent.getIntExtra("points", 0)

        // Update the success message using string resources
        val successMessage = getString(R.string.success_message, difficulty, points)
        binding.successMessage.text = successMessage

        binding.playAgainButton.setOnClickListener {
            // Start a new game
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.seeScoresButton.setOnClickListener {
            // Navigate to the scores screen
            val intent = Intent(this, ScoresActivity::class.java)
            startActivity(intent)
        }
    }
}
