package com.example.sudokuzen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var soundSwitch: Switch
    private lateinit var difficultyRadioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialiser les composants de l'UI
        soundSwitch = findViewById(R.id.soundSwitch)
        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup)
        backButton = findViewById(R.id.backButton)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Charger les paramètres sauvegardés
        loadPreferences()

        // Listener pour le Switch des sons
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("sound_enabled", isChecked)
            editor.apply()
        }

        // Listener pour les boutons radio de difficulté
        difficultyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()
            when (checkedId) {
                R.id.easyRadioButton -> editor.putString("difficulty", "Easy")
                R.id.mediumRadioButton -> editor.putString("difficulty", "Medium")
                R.id.hardRadioButton -> editor.putString("difficulty", "Hard")
            }
            editor.apply()
        }

        // Listener pour le bouton retour
        backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous one
        }
    }

    // Fonction pour charger les préférences utilisateur
    private fun loadPreferences() {
        val soundEnabled = sharedPreferences.getBoolean("sound_enabled", true)
        soundSwitch.isChecked = soundEnabled

        val difficulty = sharedPreferences.getString("difficulty", "Medium")
        when (difficulty) {
            "Easy" -> difficultyRadioGroup.check(R.id.easyRadioButton)
            "Medium" -> difficultyRadioGroup.check(R.id.mediumRadioButton)
            "Hard" -> difficultyRadioGroup.check(R.id.hardRadioButton)
        }
    }
}
