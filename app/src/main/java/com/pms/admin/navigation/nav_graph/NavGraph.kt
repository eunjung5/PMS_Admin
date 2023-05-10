package com.pms.admin.navigation.nav_graph

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.pms.admin.MainActivity
import com.pms.admin.data.api.SessionManagerUtil
import com.pms.admin.navigation.AUTH_GRAPH_ROUTE
import com.pms.admin.navigation.HOME_GRAPH_ROUTE
import com.pms.admin.navigation.ROOT_GRAPH_ROUTE
import com.pms.admin.ui.MainViewModel
import java.util.*

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
) {
    val userInfo = viewModel.loginUser.value
    Log.d(MainActivity.TAG,"main activity => result : ${userInfo.result} , authority = ${userInfo.authenticated}")
    val context = LocalContext.current

    if (userInfo.result){
        LaunchedEffect(key1 = true){
            SessionManagerUtil.startUserSession(context,600)
            Log.d(
                MainActivity.TAG, "session = ${ SessionManagerUtil.isSessionActive(
                Calendar.getInstance().time,context)},")
        }
    }

    NavHost(
        navController = navController,
        startDestination = if(userInfo.result) HOME_GRAPH_ROUTE else AUTH_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
    ){
        authNavGraph(viewModel)                 //login screen
        homeNavGraph(navController,viewModel)   //home main menu
        managerNavGraph(navController,viewModel)  //관리자 관리
    }
}