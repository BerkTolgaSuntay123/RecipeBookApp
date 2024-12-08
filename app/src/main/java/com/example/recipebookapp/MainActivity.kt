package com.example.recipebookapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recipebookapp.ui.theme.RecipeBookAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeBookAppTheme {
                RecipeApp()
            }
        }
    }
}

@Composable
fun RecipeApp() {
    val navController = rememberNavController()
    val selectedRecipe = remember { mutableStateOf<Recipe?>(null) }

    NavHost(navController = navController, startDestination = "recipeList") {
        composable("recipeList") {
            RecipeListScreen(
                onRecipeSelected = { recipe ->
                    navController.navigate("recipeDetails/${recipe.id}")
                }
            )
        }
        composable("recipeDetails/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")?.toInt() ?: 0
            RecipeDetailsScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() }
            )
        }
    }

}
