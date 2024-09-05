package com.example.sudokuzen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.sudokuzen.game.SudokuGame

class PlaySudokuViewModel(context: Context) : ViewModel() {
    var sudokuGame = SudokuGame(context) // Pass context to SudokuGame
}
