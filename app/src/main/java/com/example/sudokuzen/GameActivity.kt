package com.example.sudokuzen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import java.io.InputStreamReader
import android.widget.ArrayAdapter


class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Find the Back button by its ID
        val backButton = findViewById<Button>(R.id.backButton)
        val gridView = findViewById<GridView>(R.id.sudokuGridView)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)

        // Set a click listener on the Back button
        backButton.setOnClickListener {
            finish() // Close the current activity and return to the previous one
        }

        // Récupérer la difficulté sélectionnée depuis SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val difficulty = sharedPreferences.getString("difficulty", "Medium") ?: "Medium"

        try {
            // Charger le fichier JSON approprié en fonction de la difficulté
            val sudoku = loadSudokuFromAssets(difficulty)

            if (sudoku.sudokus.isNotEmpty()) {
                // Afficher la première grille du Sudoku (vous pouvez en choisir une autre ou aléatoirement)
                displaySudoku(gridView, sudoku.sudokus[0].grid)
                errorTextView.visibility = View.GONE // Masquer le message d'erreur
            } else {
                showError("Error: No Sudoku grids available", errorTextView, gridView)
            }
        } catch (e: Exception) {
            // Afficher un message d'erreur si le chargement échoue
            showError("Error: Failed to load Sudoku", errorTextView, gridView)
        }
    }

    private fun loadSudokuFromAssets(difficulty: String): Sudoku {
        val fileName = when (difficulty) {
            "Easy" -> "json/easysudoku.json"
            "Medium" -> "json/mediumsudoku.json"
            "Hard" -> "json/hardsudoku.json"
            else -> "json/mediumsudoku.json" // Par défaut à "Medium" si rien n'est sélectionné
        }

        val inputStream = assets.open(fileName)
        val reader = InputStreamReader(inputStream)
        return Gson().fromJson(reader, Sudoku::class.java)
    }

    private fun displaySudoku(gridView: GridView, grid: List<List<Int>>) {
        // Convertir la grille en une liste à plat (1D) pour l'adapter à l'ArrayAdapter
        val flatGrid = grid.flatten().map { if (it == 0) "" else it.toString() }

        // Créer un ArrayAdapter pour lier les données à la GridView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, flatGrid)
        gridView.adapter = adapter
    }

    private fun showError(message: String, errorTextView: TextView, gridView: GridView) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        gridView.visibility = View.GONE
    }
}
