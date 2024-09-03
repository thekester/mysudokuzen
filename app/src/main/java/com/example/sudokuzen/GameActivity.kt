package com.example.sudokuzen

import android.os.Bundle
import android.util.Log
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

        // Retrieve the difficulty selected from SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val difficulty = sharedPreferences.getString("difficulty", "Medium") ?: "Medium"

        try {
            // Load the appropriate JSON file depending on the difficulty
            val sudoku = loadSudokuFromAssets(difficulty)

            // Log the loaded Sudoku to check if it's properly loaded
            Log.d("GameActivity", "Loaded Sudoku: ${sudoku.sudokus}")

            if (sudoku.sudokus.isNotEmpty()) {
                // Display the first Sudoku grid (you can choose another or randomly)
                displaySudoku(gridView, sudoku.sudokus[0].grid)
                errorTextView.visibility = View.GONE // Hide error message
            } else {
                showError("Error: No Sudoku grids available", errorTextView, gridView)
            }
        } catch (e: Exception) {
            // Log the exception if loading fails
            Log.e("GameActivity", "Error loading Sudoku", e)
            // Display an error message if loading fails
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
        // Convert the grid into a flat list (1D) to adapt it to the ArrayAdapter
        val flatGrid = grid.flatten().map { if (it == 0) "" else it.toString() }

        // Log the grid content to check if the flatGrid contains values
        Log.d("GameActivity", "Sudoku Grid: $flatGrid")

        // Use a custom layout for each cell
        val adapter = ArrayAdapter(this, R.layout.sudoku_cell, flatGrid)
        gridView.adapter = adapter
    }

    private fun showError(message: String, errorTextView: TextView, gridView: GridView) {
        Log.d("GameActivity", "Error: $message")
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
        gridView.visibility = View.GONE
    }
}
