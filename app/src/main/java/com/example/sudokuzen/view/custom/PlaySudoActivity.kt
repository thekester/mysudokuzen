package com.example.sudokuzen.view.custom

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokuzen.databinding.ActivityPlaySudokuBinding
import com.example.sudokuzen.viewmodel.PlaySudokuViewModel
import com.example.sudokuzen.viewmodel.PlaySudokuViewModelFactory
import com.example.sudokuzen.game.Cell
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.example.sudokuzen.R

class PlaySudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var binding: ActivityPlaySudokuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use ViewBinding to inflate the layout
        binding = ActivityPlaySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register the listener for the SudokuBoardView
        binding.sudokuBoardView.registerListener(this)

        // Initialize ViewModel using the custom ViewModelFactory
        val factory = PlaySudokuViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[PlaySudokuViewModel::class.java]

        // Observe LiveData from ViewModel
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        // Initialize number buttons
        val numberButtons = listOf(binding.oneButton, binding.twoButton, binding.threeButton,
            binding.fourButton, binding.fiveButton, binding.sixButton,
            binding.sevenButton, binding.eightButton, binding.nineButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }

        binding.notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        binding.deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }

        // Adjust Sudoku board size based on screen size
        adjustSudokuBoardSize()


    }


    private fun adjustSudokuBoardSize() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // Get screen dimensions in pixels
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Calculate the optimal size for the Sudoku board (e.g., 80% of the smaller screen dimension)
        val boardSize = (minOf(screenWidth, screenHeight) * 0.58).toInt()

        // Set the size of the Sudoku board dynamically
        val layoutParams = binding.sudokuBoardView.layoutParams
        layoutParams.width = boardSize
        layoutParams.height = boardSize
        binding.sudokuBoardView.layoutParams = layoutParams
    }

    private fun updateCells(cells: List<Cell>?) {
        cells?.let {
            binding.sudokuBoardView.updateCells(cells)
        }
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) {
        cell?.let {
            binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)

            // Check if the selected cell is an initial cell
            val isInitialCell = viewModel.sudokuGame.isInitialCell(cell.first, cell.second)

            // Disable the delete button if the selected cell is an initial cell
            binding.deleteButton.isEnabled = !isInitialCell

            // Disable or enable the number buttons based on whether the cell is initial or not
            val numberButtons = listOf(binding.oneButton, binding.twoButton, binding.threeButton,
                binding.fourButton, binding.fiveButton, binding.sixButton,
                binding.sevenButton, binding.eightButton, binding.nineButton)

            numberButtons.forEach { button ->
                button.isEnabled = !isInitialCell
            }

            // Hide the note-taking button if the selected cell is an initial cell, otherwise show it
            if (isInitialCell) {
                binding.notesButton.visibility = View.GONE  // Hide the button
            } else {
                binding.notesButton.visibility = View.VISIBLE  // Show the button
            }
        }
    }


    // Apply gray filter based on API level compatibility
    private fun applyGrayFilter(button: View, shouldGrayOut: Boolean) {
        if (shouldGrayOut) {
            val color = Color.LTGRAY
            val colorFilter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)
            } else {
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
            button.background.colorFilter = colorFilter
        } else {
            // Clear the color filter when the button is enabled
            button.background.clearColorFilter()
        }
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) {
        isNoteTaking?.let {
            val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary) else Color.LTGRAY
            binding.notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun updateHighlightedKeys(set: Set<Int>?) {
        set?.let {
            val numberButtons = listOf(binding.oneButton, binding.twoButton, binding.threeButton,
                binding.fourButton, binding.fiveButton, binding.sixButton,
                binding.sevenButton, binding.eightButton, binding.nineButton)

            numberButtons.forEachIndexed { index, button ->
                val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary) else Color.LTGRAY
                button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    /*fun handleBackButton(view: View) {
        onBackPressed()
    }*/

    // Update the back button handler
    fun handleBackButton(view: View) {
        onBackPressedDispatcher.onBackPressed() // Use the new onBackPressedDispatcher to handle the back button
    }
}
