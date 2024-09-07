package com.example.sudokuzen.game

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.util.Log
import androidx.preference.PreferenceManager
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

    private val board: Board

    init {
        // Retrieve difficulty from SharedPreferences
        val difficulty = getDifficultyFromPreferences()

        // Load the grid based on difficulty
        val sudokuGrid = loadSudokuFromAssets(difficulty)
        val cells = sudokuGrid.flatten().mapIndexed { index, value ->
            Cell(index / 9, index % 9, value)
        }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    // Method to get the difficulty level from SharedPreferences
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

    // User input management
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            // In note-taking mode, add or remove the number from notes
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            // Check rules before adding the input
            val validMove = isValidMove(selectedRow, selectedCol, number)
            cell.value = number
            cell.isValid = validMove

            if (!validMove) {
                Log.d("SudokuGame", "Move is invalid for number $number at row $selectedRow, column $selectedCol.")
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
        if (isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
            cell.isValid = true // Resets cell validity when deleted
        }
        cellsLiveData.postValue(board.cells)
    }
}

// Data models for JSON parsing
data class Sudoku(val sudokus: List<SudokuGrid>)
data class SudokuGrid(val id: Int, val difficulty: String, val grid: List<List<Int>>)
