package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pms.admin.navigation.AUTH_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.pages.LogInScreen


fun NavGraphBuilder.authNavGraph() {
    navigation(
        startDestination = Screen.Login.route,
        route = AUTH_GRAPH_ROUTE
    ){
        composable(
            route = Screen.Login.route
        ){
            LogInScreen()
        }
    }
}