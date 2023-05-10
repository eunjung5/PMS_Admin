package com.pms.admin.navigation.nav_graph

import android.util.Log
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.model.Mode
import com.pms.admin.navigation.MANAGER_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.MainViewModel
import com.pms.admin.ui.views.managerManagement.ManagerAddEdit
import com.pms.admin.ui.views.managerManagement.ManagerJobSearch
import com.pms.admin.ui.views.managerManagement.ManagerManagement
import com.pms.admin.ui.views.managerManagement.ManagerPasswordEdit

fun NavGraphBuilder.managerNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
) {
    navigation(
        startDestination = Screen.ManagerManagement.route,
        route = MANAGER_GRAPH_ROUTE,
    ){
        //관리자 리스트
        composable(
            route = Screen.ManagerManagement.route
        ){
            ManagerManagement(navController,viewModel)
        }

        //관리자 생성
        composable(
            route = Screen.ManagerAdd.route
        ){
            ManagerAddEdit(navController, viewModel,"")
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

            ManagerAddEdit(navController, viewModel, userId)
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

            ManagerPasswordEdit(navController, viewModel, userId)
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

           ManagerJobSearch(navController, viewModel, userId)
        }
    }
}