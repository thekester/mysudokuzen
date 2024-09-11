package com.example.sudokuzen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sudokuzen.SettingsActivity
import android.net.Uri
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button to begin a new game
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            // Navigate to the PlaySudokuActivity
            val intent = Intent(this, com.example.sudokuzen.view.custom.PlaySudokuActivity::class.java)
            startActivity(intent)
        }

        // Button to view scores
        val scoresButton = findViewById<Button>(R.id.scoresButton)
        scoresButton.setOnClickListener {
            // Navigate to the scores activity
            val intent = Intent(this, ScoresActivity::class.java)
            startActivity(intent)
        }

        // Button to access settings
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            // Navigate to the settings activity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val buyMeCoffeeIcon: ImageView = findViewById(R.id.buyMeCoffeeIcon)
        buyMeCoffeeIcon.setOnClickListener {
            val url = "https://www.buymeacoffee.com/tavenel"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

}
