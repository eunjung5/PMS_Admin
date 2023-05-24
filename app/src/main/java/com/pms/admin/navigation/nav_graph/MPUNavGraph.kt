package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pms.admin.model.data.Mode
import com.pms.admin.navigation.MPU_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.component.common.CheckAuthorityAndFinish
import com.pms.admin.ui.views.mpuManagement.MPUAddEdit
import com.pms.admin.ui.views.mpuManagement.MPUManagement

fun NavGraphBuilder.mpuNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = Screen.MPUManagement.route,
        route = MPU_GRAPH_ROUTE,
    ) {

        //mpu 리스트
        composable(
            route = Screen.MPUManagement.route
        ) {

            CheckAuthorityAndFinish()
            MPUManagement(navController)
        }

        //mpu 생성
        composable(
            route = Screen.MPUAdd.route,
        ) {
            CheckAuthorityAndFinish()
            MPUAddEdit(navController = navController, mpuId = "", mode = Mode.Add)
        }

        //mpu 수정
        composable(
            route = Screen.MPUEdit.route,
            arguments = listOf(
                navArgument("mpuId") {
                    type = NavType.StringType
                }
            )
        ) {entry ->
            val mpuId = entry.arguments?.getString("mpuId") ?: ""

            CheckAuthorityAndFinish()
            MPUAddEdit(navController = navController, mpuId = mpuId, mode = Mode.Edit)
        }

    }
}