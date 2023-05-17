package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pms.admin.navigation.HOME_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.views.mainmenu.MainMenu

fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
) {
   navigation(
        startDestination = Screen.Home.route,
        route = HOME_GRAPH_ROUTE
    ){
        composable(
            route = Screen.Home.route
        ){
            MainMenu(navController)
        }
    }
}