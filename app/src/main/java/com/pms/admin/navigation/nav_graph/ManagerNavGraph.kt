package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pms.admin.navigation.MANAGER_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.component.common.CheckAuthorityAndFinish
import com.pms.admin.ui.views.managerManagement.*

fun NavGraphBuilder.managerNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = Screen.ManagerManagement.route,
        route = MANAGER_GRAPH_ROUTE,
    ){
        //관리자 리스트
        composable(
            route = Screen.ManagerManagement.route
        ){
            CheckAuthorityAndFinish()
            ManagerManagement(navController)
        }

        //관리자 생성
        composable(
            route = Screen.ManagerAdd.route
        ){
            CheckAuthorityAndFinish()
            ManagerAddEdit(navController,"")
        }

        //관리자 수정
        composable(
            route = Screen.ManagerEdit.route,
            arguments= listOf(
                navArgument("userId"){
                    type = NavType.StringType
                }
            )
        ){  entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            CheckAuthorityAndFinish()
            ManagerAddEdit(navController, userId)
        }

        //관리자 PW 변경
        composable(
            route = Screen.ManagerPasswordEdit.route,
            arguments= listOf(
                navArgument("userId"){
                    type = NavType.StringType
                }
            )
        ){  entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            CheckAuthorityAndFinish()
            ManagerPasswordEdit(navController, userId)
        }

        //관리자 작업 조회
        composable(
            route = Screen.ManagerJobSearch.route,
            arguments= listOf(
                navArgument("userId"){
                    type = NavType.StringType
                }
            )
        ){  entry ->
            val userId = entry.arguments?.getString("userId") ?: ""
            CheckAuthorityAndFinish()
            ManagerJobSearch(navController, userId)
        }
    }
}