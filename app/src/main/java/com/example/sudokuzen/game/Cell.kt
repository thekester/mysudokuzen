package com.example.sudokuzen.game

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf<Int>(),
    var isValid: Boolean = true  // Ajoute cette propriété pour indiquer si la cellule est valide
)
