package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pms.admin.model.Mode
import com.pms.admin.navigation.SITE_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.views.siteManagement.SiteAddEdit
import com.pms.admin.ui.views.siteManagement.SiteMPUAddDelete
import com.pms.admin.ui.views.siteManagement.SiteManagement


fun NavGraphBuilder.siteNavGraph(
    navController: NavHostController,
) {

    navigation(
        startDestination = Screen.SiteManagement.route,
        route = SITE_GRAPH_ROUTE,
    ) {
        //site 리스트
        composable(
            route = Screen.SiteManagement.route
        ) {
            SiteManagement(navController, )
        }

        //site 생성
        composable(
            route = Screen.SiteAdd.route
        ) {
            SiteAddEdit(navController = navController, siteId="")
        }

        //site 수정
        composable(
            route = Screen.SiteEdit.route,
            arguments = listOf(
                navArgument("siteId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val siteId = entry.arguments?.getString("siteId") ?: ""
            SiteAddEdit(navController = navController,siteId= siteId)
        }

        //site - MPU 추가
        composable(
            route = Screen.SiteMPUAdd.route,
            arguments = listOf(
                navArgument("siteId") {
                    type = NavType.StringType
                },
                        navArgument("siteName") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { entry ->
            val siteId = entry.arguments?.getString("siteId") ?: ""
            val siteName = entry.arguments?.getString("siteName") ?: ""

            SiteMPUAddDelete(navController = navController,siteId= siteId, siteName= siteName, Mode.Add)
        }

    }
}