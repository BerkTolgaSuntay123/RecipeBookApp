package com.example.recipebookapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun RecipeListScreen(onRecipeSelected: (Recipe) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var apiRecipes by remember { mutableStateOf<List<RecipeResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Book") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Recipes") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        apiRecipes = try {
                            fetchRecipesFromApi(searchQuery)
                        } catch (e: Exception) {
                            errorMessage = "Failed to fetch recipes. Please try again."
                            emptyList()
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("Search")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(apiRecipes) { recipeResult ->
                        val recipe = Recipe(
                            id = recipeResult.id,
                            title = recipeResult.title,
                            description = recipeResult.summary ?: "No description available", // Handle nullable description
                            ingredients = recipeResult.ingredients?.joinToString(separator = ", ") ?: "No ingredients available", // Handle nullable list
                            steps = recipeResult.instructions ?: "No steps available", // Handle nullable instructions
                            image = recipeResult.image ?: "" // Ensure image is included
                        )
                        RecipeItem(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Display recipe image
            Image(
                painter = rememberAsyncImagePainter(recipe.image),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            // Display recipe title
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}

suspend fun fetchRecipesFromApi(query: String): List<RecipeResult> {
    return try {
        RetrofitInstance.api.searchRecipes(query, "0d70f98785b446cc90afddcbeee4fd82").results.map { result ->
            RecipeResult(
                id = result.id,
                title = result.title,
                image = result.image,
                summary = result.summary,
                instructions = result.instructions ?: "No steps available",
                ingredients = result.ingredients ?: listOf("No ingredients available")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}


//suspend fun fetchRecipeDetails(recipeId: Int): RecipeResult {
//    return try {
//        val response = RetrofitInstance.api.getRecipeInformation(recipeId, "0d70f98785b446cc90afddcbeee4fd82")
//        RecipeResult(
//            id = response.id,
//            title = response.title,
//            image = response.image,
//            summary = response.summary ?: "No description available",
//            instructions = response.instructions ?: "No steps available",
//            ingredients = response.extendedIngredients?.map { it.original } ?: listOf("No ingredients available")
//        )
//    } catch (e: Exception) {
//        e.printStackTrace()
//        RecipeResult(
//            id = recipeId,
//            title = "No title available",
//            image = "",
//            summary = "No description available",
//            instructions = "No steps available",
//            ingredients = listOf("No ingredients available")
//        )
//    }
//}


suspend fun fetchRecipeDetails(recipeId: Int): Recipe {
    return try {
        val response = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey = "0d70f98785b446cc90afddcbeee4fd82")
        Recipe(
            id = response.id,
            title = response.title,
            description = response.summary ?: "No description available",
            ingredients = response.extendedIngredients?.joinToString(separator = ", ") { it.original } ?: "No ingredients available",
            steps = response.instructions ?: "No steps available",
            image = response.image ?: "" // Include image parameter here
        )
    } catch (e: Exception) {
        e.printStackTrace()
        Recipe(
            id = recipeId,
            title = "No title available",
            description = "No description available",
            ingredients = "No ingredients available",
            steps = "No steps available",
            image = "" // Provide a fallback empty string for the image
        )
    }
}




