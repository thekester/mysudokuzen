package com.example.sudokuzen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sudokuzen.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bouton pour commencer un nouveau jeu
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            // Naviguer vers l'activité du jeu
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Bouton pour voir les scores
        val scoresButton = findViewById<Button>(R.id.scoresButton)
        scoresButton.setOnClickListener {
            // Naviguer vers l'activité des scores
            val intent = Intent(this, ScoresActivity::class.java)
            startActivity(intent)
        }

        // Bouton pour accéder aux paramètres
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            // Naviguer vers l'activité des paramètres
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
