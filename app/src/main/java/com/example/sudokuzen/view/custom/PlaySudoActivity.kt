package com.example.sudokuzen.view.custom

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sudokuzen.databinding.ActivityPlaySudokuBinding
import com.example.sudokuzen.viewmodel.PlaySudokuViewModel
import com.example.sudokuzen.viewmodel.PlaySudokuViewModelFactory
import com.example.sudokuzen.game.Cell
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
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
    }

    private fun updateCells(cells: List<Cell>?) {
        cells?.let {
            binding.sudokuBoardView.updateCells(cells)
        }
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) {
        cell?.let {
            binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
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

    // The system's default back button behavior can still be overridden if necessary
    override fun onBackPressed() {
        super.onBackPressed()
        // Optionally add custom logic here if you want to handle back press
    }

    // This method is used in the XML layout to handle the back button click
    fun handleBackButton(view: View) {
        // Call the system back button action
        onBackPressed()
    }
}
