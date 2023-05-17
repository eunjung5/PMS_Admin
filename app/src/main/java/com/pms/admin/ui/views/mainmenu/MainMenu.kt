package com.pms.admin.ui.views.mainmenu

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.component.menu.MainMenuItem
import kotlinx.coroutines.launch

@Composable
fun MainMenu(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
) {
    //LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MainMenuHeader(viewModel)

        MainMeuList { url ->
            navController.navigate(url)
        }

        MainMenuBottom()
    }
}

//MainMenu Header
@Composable
fun ColumnScope.MainMenuHeader(viewModel: MainViewModel) {

    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    val window = rememberWindowSize()

    Column(
        modifier = Modifier
            .weight(2f)
            .fillMaxSize()
            .padding(top = if (window.height == WindowType.Medium) 30.dp else 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "Admin",
                color = Color.Red,
                fontSize = if (window.height == WindowType.Medium) MaterialTheme.typography.h4.fontSize else MaterialTheme.typography.h6.fontSize,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Logout",
                    color = Color.White,
                    fontSize = if (window.height == WindowType.Medium) MaterialTheme.typography.h6.fontSize else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .height(if (window.height == WindowType.Medium) 50.dp else 30.dp)
                        .width(if (window.height == WindowType.Medium) 50.dp else 30.dp)
                        .clickable {
                            viewModel.logout()

                            scope.launch {
                                viewModel.logoutResult.collect {
                                    Log.d(TAG, "logoutResult  =  $it")
                                    activity?.finish()
                                }
                            }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        //header line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Red)
        ) {}
    }
}

//MainMenu Icon List
@Composable
fun ColumnScope.MainMeuList(
    onNavigate: (String) -> Unit = {}
) {
    val window = rememberWindowSize()

    Column(
        modifier = Modifier
            .weight(8f)
            .fillMaxSize()
            .padding(if (window.height == WindowType.Medium) 30.dp else 10.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            MainMenuItem(
                "관리자 관리",
                painterResource(id = R.drawable.main_user),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }

            Spacer(modifier = Modifier.width(10.dp))
            MainMenuItem(
                "사이트 관리",
                painterResource(id = R.drawable.main_site),
                "site_management"
            ) { route ->
                onNavigate(route)
            }

            Spacer(modifier = Modifier.width(10.dp))
            MainMenuItem(
                "MPU 관리",
                painterResource(id = R.drawable.main_mpu),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            MainMenuItem(
                "관리자 작업조회",
                painterResource(id = R.drawable.main_search),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }
            Spacer(modifier = Modifier.width(10.dp))
            MainMenuItem(
                "KESCO 관리",
                painterResource(id = R.drawable.main_kesco),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }
            Spacer(modifier = Modifier.width(10.dp))
            MainMenuItem(
                "SMS 발송조회",
                painterResource(id = R.drawable.main_message),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }
            Spacer(modifier = Modifier.width(10.dp))
            MainMenuItem(
                "Admin PW변경",
                painterResource(id = R.drawable.main_lock),
                "manager_management"
            ) { route ->
                onNavigate(route)
            }
        }
    }

}

//MainMenu Bottom line
@Composable
fun ColumnScope.MainMenuBottom() {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Red)
        ) {}
    }

}

@Preview(showBackground = true, backgroundColor = 0x000000, device = Devices.AUTOMOTIVE_1024p)
@Composable
fun DefaultPreview() {

}