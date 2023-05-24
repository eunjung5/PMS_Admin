package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pms.admin.navigation.*
import com.pms.admin.ui.component.common.CheckAuthorityAndFinish
import com.pms.admin.ui.views.changeAdminPassword.ChangeAdminPassword

fun NavGraphBuilder.changePasswordNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = Screen.ChangeAdminPassword.route,
        route = CHANGE_PASSWORD_GRAPH_ROUTE,
    ) {

        //Admin PW 변경
        composable(
            route = Screen.ChangeAdminPassword.route
        ) {

            CheckAuthorityAndFinish()
            ChangeAdminPassword(navController)
        }
    }

}