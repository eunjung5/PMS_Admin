package com.pms.admin.navigation.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.pms.admin.navigation.JOB_HISTORY_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.component.common.CheckAuthorityAndFinish
import com.pms.admin.ui.views.jobHistory.InquiryByControl
import com.pms.admin.ui.views.jobHistory.InquiryByManager
import com.pms.admin.ui.views.jobHistory.JobHistory


fun NavGraphBuilder.jobHistoryNavGraph(
    navController: NavHostController,
) {
    navigation(
        startDestination = Screen.JobHistory.route,
        route = JOB_HISTORY_GRAPH_ROUTE,
    ) {

        //관리자 작업조회
        composable(
            route = Screen.JobHistory.route
        ) {
            CheckAuthorityAndFinish()
            JobHistory(navController)
        }

        //관리자별 조회
        composable(
            route = Screen.InquiryByManager.route
        ) {
            CheckAuthorityAndFinish()
            InquiryByManager(navController)
        }

        //제어 이력 조회
        composable(
            route = Screen.InquiryByControlHistory.route
        ) {
            CheckAuthorityAndFinish()
            InquiryByControl(navController)
        }
    }
}