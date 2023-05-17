package com.pms.admin.navigation.nav_graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.pms.admin.data.api.SessionManagerUtil
import com.pms.admin.navigation.AUTH_GRAPH_ROUTE
import com.pms.admin.navigation.HOME_GRAPH_ROUTE
import com.pms.admin.navigation.ROOT_GRAPH_ROUTE
import com.pms.admin.ui.viewModels.MainViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(),
) {
    val userInfo = viewModel.loginUser.value
    val context = LocalContext.current

    if (userInfo.result){
        LaunchedEffect(key1 = true){
            SessionManagerUtil.startUserSession(context,600)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if(userInfo.result) HOME_GRAPH_ROUTE else AUTH_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
    ){

        authNavGraph()                 //login screen
        homeNavGraph(navController)   //home main menu
        managerNavGraph(navController)  //관리자 관리
        siteNavGraph(navController)    //사이트 관리
    }
}