package com.example.sudokuzen.game

import android.content.Context
import androidx.lifecycle.MutableLiveData
import android.util.Log
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
        val sudokuGrid = loadSudokuFromAssets("Medium") // Load Medium as default
        val cells = sudokuGrid.flatten().mapIndexed { index, value ->
            Cell(index / 9, index % 9, value)
        }
        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    private fun loadSudokuFromAssets(difficulty: String): List<List<Int>> {
        val fileName = when (difficulty) {
            "Easy" -> "json/easysudoku.json"
            "Medium" -> "json/mediumsudoku.json"
            "Hard" -> "json/hardsudoku.json"
            else -> "json/mediumsudoku.json"
        }

        try {
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val sudoku = Gson().fromJson(reader, Sudoku::class.java)
            reader.close()
            return sudoku.sudokus[0].grid
        } catch (e: Exception) {
            Log.e("SudokuGame", "Error loading Sudoku from assets", e)
            throw e
        }
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

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

    fun delete() {
        val cell = board.getCell(selectedRow, selectedCol)
        if (isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
        }
        cellsLiveData.postValue(board.cells)
    }
}

data class Sudoku(val sudokus: List<SudokuGrid>)
data class SudokuGrid(val id: Int, val difficulty: String, val grid: List<List<Int>>)
