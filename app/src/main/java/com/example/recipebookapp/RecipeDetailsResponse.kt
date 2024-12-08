package com.example.recipebookapp

data class RecipeDetailsResponse(
    val id: Int,
    val title: String,
    val image: String,
    val summary: String?,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>?
)

data class Ingredient(
    val id: Int,
    val original: String // The full ingredient description (e.g., "2 cups of flour")
)

