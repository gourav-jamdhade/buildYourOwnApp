package com.example.queueview.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.queueview.presentation.screens.FormScreen
import com.example.queueview.presentation.screens.NearbyLocationScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "nearby"
    ) {
        composable("nearby") {
            NearbyLocationScreen(
                viewModel = koinViewModel(),
                onAddDataClick = { navController.navigate("form") }
            )
        }
        composable("form",
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) }) {
            FormScreen(
                viewModel = koinViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}