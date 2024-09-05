package com.example.sudokuzen.viewmodel

import androidx.lifecycle.ViewModel // Use AndroidX's ViewModel
import com.example.sudokuzen.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}
