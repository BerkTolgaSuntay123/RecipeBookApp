package com.example.recipebookapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailsScreen(recipeId: Int, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(recipeId) {
        coroutineScope.launch {
            try {
                recipe = fetchRecipeDetails(recipeId)
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Failed to load recipe details"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.title ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            recipe?.let {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    item {
                        // Add Recipe Image
                        AsyncImage(
                            model = it.image, // Image URL from the Recipe object
                            contentDescription = "Recipe Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 16.dp),
                            contentScale = ContentScale.Crop
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    item {
                        Text("Description:", style = MaterialTheme.typography.h6)
                        Text(
                            it.description,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    item {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Ingredients:", style = MaterialTheme.typography.h6)
                        it.ingredients.split(", ").forEach { ingredient ->
                            Text("- $ingredient", style = MaterialTheme.typography.body1)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Steps:", style = MaterialTheme.typography.h6)
                        Text(
                            it.steps,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}
