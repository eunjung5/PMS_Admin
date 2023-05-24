package com.pms.admin.navigation.nav_graph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.pms.admin.model.data.Mode
import com.pms.admin.navigation.SITE_GRAPH_ROUTE
import com.pms.admin.navigation.Screen
import com.pms.admin.ui.views.siteManagement.SiteAddEdit
import com.pms.admin.ui.views.siteManagement.SiteMPUAddDelete
import com.pms.admin.ui.views.siteManagement.SiteManagement
import com.pms.admin.ui.views.siteManagement.SiteManagerAddDelete
import com.pms.admin.R
import com.pms.admin.ui.component.common.CheckAuthorityAndFinish
import com.pms.admin.ui.theme.ContentsBackground

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
            CheckAuthorityAndFinish()
            SiteManagement(navController)
        }

        //site 생성
        composable(
            route = Screen.SiteAdd.route
        ) {

            CheckAuthorityAndFinish()
            SiteAddEdit(navController = navController, siteId = "", mode = Mode.Add)
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
            CheckAuthorityAndFinish()
            SiteAddEdit(navController = navController, siteId = siteId, mode = Mode.Edit)
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
            CheckAuthorityAndFinish()
            SiteMPUAddDelete(
                navController = navController,
                siteId = siteId,
                siteName = siteName,
                mode = Mode.Add
            )
        }

        //site - MPU 삭제
        composable(
            route = Screen.SiteMPUDelete.route,
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

            CheckAuthorityAndFinish()
            SiteMPUAddDelete(
                navController = navController,
                siteId = siteId,
                siteName = siteName,
                mode = Mode.Delete
            )
        }

        //site - 사용자 추가
        composable(
            route = Screen.SiteManagerAdd.route,
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

            CheckAuthorityAndFinish()
            SiteManagerAddDelete(
                navController = navController,
                siteId = siteId,
                siteName = siteName,
                mode = Mode.Add
            )
        }

        //site - 사용자 삭제
        composable(
            route = Screen.SiteManagerDelete.route,
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

            CheckAuthorityAndFinish()
            SiteManagerAddDelete(
                navController = navController,
                siteId = siteId,
                siteName = siteName,
                mode = Mode.Delete
            )
        }

    }
}


@Preview
@Composable
fun popup() {
    Column(
        modifier = Modifier
            .width(400.dp)
            .height(300.dp)
            .background(ContentsBackground)
            .padding(30.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF212529))
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.manager_notify),
                color = Color.White,
                fontSize = MaterialTheme.typography.h6.fontSize,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Column(modifier = Modifier.padding(start = 30.dp, top = 30.dp)) {
            Text(stringResource(R.string.no_authority_message), color = Color.White)

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 10.dp,end = 30.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(id = R.string.confirm))
                }
            }
        }
    }


}