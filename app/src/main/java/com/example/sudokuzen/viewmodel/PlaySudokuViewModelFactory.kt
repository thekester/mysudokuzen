package com.example.sudokuzen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaySudokuViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaySudokuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaySudokuViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
