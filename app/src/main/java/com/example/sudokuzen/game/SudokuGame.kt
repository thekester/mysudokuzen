package com.example.sudokuzen.game

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.sudokuzen.SuccessActivity
import com.google.gson.Gson
import java.io.InputStreamReader

class SudokuGame(private val context: Context) {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    // Define points for each difficulty level
    private val EASY_POINTS = 10
    private val MEDIUM_POINTS = 20
    private val HARD_POINTS = 30

    private val board: Board

    // List to store the initial cell positions
    private val initialCellsPositions = mutableSetOf<Pair<Int, Int>>()

    init {
        // Retrieve difficulty from SharedPreferences
        val difficulty = getDifficultyFromPreferences()

        // Load the grid based on difficulty
        val sudokuGrid = loadSudokuFromAssets(difficulty)
        val cells = sudokuGrid.flatten().mapIndexed { index, value ->
            val cell = Cell(index / 9, index % 9, value)

            // Store initial cell positions where value is not zero
            if (cell.value != 0) {
                initialCellsPositions.add(Pair(cell.row, cell.col))
            }

            cell
        }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    // Check if a cell is an initial cell
    fun isInitialCell(row: Int, col: Int): Boolean {
        return initialCellsPositions.contains(Pair(row, col))
    }

    // Method to get the difficulty level from SharedPreferences
    // SudokuGame.kt
    private fun getDifficultyFromPreferences(): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("difficulty", "Medium") ?: "Medium" // Default to "Medium"
    }


    // Load a random Sudoku file based on difficulty
    private fun loadSudokuFromAssets(difficulty: String): List<List<Int>> {
        val randomIndex = (1..10).random()
        val fileName = when (difficulty) {
            "Easy" -> "json/easy/sudoku$randomIndex.json"
            "Medium" -> "json/medium/sudoku$randomIndex.json"
            "Hard" -> "json/hard/sudoku$randomIndex.json"
            else -> "json/medium/sudoku$randomIndex.json" // Default to "Medium"
        }

        return try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val sudoku = Gson().fromJson(reader, Sudoku::class.java)
            reader.close()
            sudoku.sudokus[0].grid
        } catch (e: Exception) {
            Log.e("SudokuGame", "Error loading Sudoku from assets: $fileName", e)
            throw e
        }
    }

    // Function to check if an input is valid (no repetition in row, column, or 3x3 box)
    private fun isValidMove(row: Int, col: Int, number: Int): Boolean {
        // Check row
        for (c in 0 until 9) {
            if (board.getCell(row, c).value == number && c != col) {
                Log.d("SudokuGame", "Invalid move: Number $number already exists in row $row at column $c.")
                return false
            }
        }

        // Check column
        for (r in 0 until 9) {
            if (board.getCell(r, col).value == number && r != row) {
                Log.d("SudokuGame", "Invalid move: Number $number already exists in column $col at row $r.")
                return false
            }
        }

        // Check 3x3 box
        val startRow = (row / 3) * 3
        val startCol = (col / 3) * 3
        for (r in startRow until startRow + 3) {
            for (c in startCol until startCol + 3) {
                if (board.getCell(r, c).value == number && (r != row || c != col)) {
                    Log.d("SudokuGame", "Invalid move: Number $number already exists in the 3x3 box starting at row $startRow, column $startCol, found at row $r, column $c.")
                    return false
                }
            }
        }

        // If no rule is broken
        return true
    }

    // Check if the game is completed
    fun isGameCompleted(): Boolean {
        for (cell in board.cells) {
            if (cell.value == 0 || !cell.isValid) {
                return false
            }
        }
        return true
    }

    // Increase the score by 1 and save it in SharedPreferences
    // SudokuGame.kt
    fun increaseScore(points: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val currentScore = sharedPreferences.getInt("score", 0)
        sharedPreferences.edit().putInt("score", currentScore + points).apply()
    }


    // User input management
    // SudokuGame.kt
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            // Note-taking mode logic
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            // Normal input mode
            val validMove = isValidMove(selectedRow, selectedCol, number)
            cell.value = number
            cell.isValid = validMove

            if (!validMove) {
                Log.d("SudokuGame", "Move is invalid for number $number at row $selectedRow, column $selectedCol.")
            }

            // Check if the game is completed after the move
            if (isGameCompleted()) {
                val difficulty = getDifficultyFromPreferences()
                val points = when (difficulty) {
                    "Easy" -> EASY_POINTS
                    "Medium" -> MEDIUM_POINTS
                    "Hard" -> HARD_POINTS
                    else -> MEDIUM_POINTS // Default to Medium
                }
                increaseScore(points)
                val intent = Intent(context, SuccessActivity::class.java).apply {
                    putExtra("difficulty", difficulty)
                    putExtra("points", points)
                }
                context.startActivity(intent)
            }
        }
        cellsLiveData.postValue(board.cells)
    }


    // Update the selected cell
    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    // Toggle note-taking mode
    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    // Delete the content of a cell
    fun delete() {
        val cell = board.getCell(selectedRow, selectedCol)

        // Prevents deletion of values in initial cells
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
            cell.isValid = true // Resets the validity of the cell after deleting the values
        }
        cellsLiveData.postValue(board.cells)
    }

    fun getCell(row: Int, col: Int): Cell {
        return board.getCell(row, col)
    }

}

// Data models for JSON parsing
data class Sudoku(val sudokus: List<SudokuGrid>)
data class SudokuGrid(val id: Int, val difficulty: String, val grid: List<List<Int>>)
