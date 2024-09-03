package com.example.sudokuzen

data class Sudoku(val sudokus: List<Grid>)

data class Grid(val id: Int, val difficulty: String, val grid: List<List<Int>>)
