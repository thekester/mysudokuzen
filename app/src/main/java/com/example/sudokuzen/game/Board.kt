package com.example.sudokuzen.game

class Board(val size: Int, val cells: List<Cell>) {


    fun getCell(row: Int, col: Int): Cell {
        // Vérifier que les indices sont valides
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw IndexOutOfBoundsException("Index out of bounds: row=$row, col=$col")
        }
        return cells[row * size + col]
    }
}