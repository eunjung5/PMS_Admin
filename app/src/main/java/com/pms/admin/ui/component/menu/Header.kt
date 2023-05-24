package com.pms.admin.ui.component.menu

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.viewModels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.Header(
    navController: NavHostController,
    viewModel: MainViewModel,
    title: String,
    iconUrl: Int
) {

    val scope = rememberCoroutineScope()
    val activity = (LocalContext.current as? Activity)
    val window = rememberWindowSize()

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(
                top = if (window.height == WindowType.Medium) 30.dp else 10.dp,
                start = 15.dp,
                end = 15.dp
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Admin",
                    color = Color.Red,
                    fontSize = if (window.height == WindowType.Medium) 20.sp else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 15.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(top = if (window.height == WindowType.Medium) 0.dp else 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Icon(
                    painter = painterResource(id = iconUrl),
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier
                        .height(if (window.height == WindowType.Medium) 50.dp else 30.dp)
                        .width(if (window.height == WindowType.Medium) 50.dp else 30.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    title,
                    color = Color.Red,
                    fontSize = if (window.height == WindowType.Medium) 30.sp else 20.sp,
                    fontWeight = FontWeight.ExtraBold,

                    )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    "이전",
                    color = Color.White,
                    fontSize = if (window.height == WindowType.Medium) 20.sp else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Column(modifier = Modifier.padding(top = 15.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .height(if (window.height == WindowType.Medium) 30.dp else 20.dp)
                            .width(if (window.height == WindowType.Medium) 30.dp else 20.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    "logout",
                    color = Color.White,
                    fontSize = if (window.height == WindowType.Medium) 20.sp else 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Column(modifier = Modifier.padding(top = 15.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .height(if (window.height == WindowType.Medium) 30.dp else 20.dp)
                            .width(if (window.height == WindowType.Medium) 30.dp else 20.dp)
                            .clickable {
                                viewModel.logout()

                                scope.launch {
                                    viewModel.logoutResult.collect {
                                        Log.d(MainActivity.TAG, "logoutResult  =  $it")
                                        activity?.finish()
                                    }
                                }

                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
fun DefaultPreview1() {

}