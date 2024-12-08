package com.example.recipebookapp

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: String,
    val steps: String,
    val image: String
)
