package com.example.recipebookapp

data class RecipeResponse(
    val results: List<RecipeResult>
)

data class RecipeResult(
    val id: Int,
    val title: String,
    val image: String,
    val summary: String?, // For description
    val instructions: String?, // For steps
    val ingredients: List<String>? // Update if ingredients are part of the response
)


