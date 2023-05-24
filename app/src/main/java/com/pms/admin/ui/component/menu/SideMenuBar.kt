package com.pms.admin.ui.component.menu

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize

data class IconLink(
    val icon: Int,
    val url: String
)


@Composable
fun SidebarMenu(
    navController:NavHostController,
    selected: Int, //menu index
) {
    var selectedItem by remember { mutableStateOf(selected) }
    val icons = listOf(
        IconLink(R.drawable.menu_manager, "manager_management"),
        IconLink(R.drawable.menu_site, "site_management"),
        IconLink(R.drawable.menu_mpu, "mpu_management"),
        IconLink(R.drawable.menu_search, "job_history"),
        IconLink(R.drawable.menu_kesco, ""),
        IconLink(R.drawable.menu_message, ""),
        IconLink(R.drawable.menu_lock, "change_admin_password")
    )
    val window = rememberWindowSize()

    NavigationRail(
        modifier = Modifier.fillMaxHeight()
            .clickable {
                navController.navigate("home_screen"){
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                 }
            },
        backgroundColor = Color(0xFF38373d),
        header = {
            Spacer(modifier = Modifier.height(if (window.height == WindowType.Medium) 30.dp else 10.dp))

            Icon(
                painter = painterResource(id = R.drawable.menu_home),
                contentDescription = "",
                modifier = Modifier
                    .height(if (window.height == WindowType.Medium) 45.dp else 35.dp)
                    .width(if (window.height == WindowType.Medium) 45.dp else 35.dp),
                tint = Color.White,

            )
            Spacer(modifier = Modifier.height(if (window.height == WindowType.Medium) 30.dp else 10.dp))
        },

        ) {

        icons.forEachIndexed { index, item ->
            NavigationRailItem(
                modifier = Modifier.height(if (window.height == WindowType.Medium) 70.dp else 40.dp),
                icon = {
                    Icon(
                        painter = painterResource(id = icons[index].icon),
                        contentDescription = "",
                        modifier = Modifier
                            .height(if (window.height == WindowType.Medium) 35.dp else 25.dp)
                            .width(if (window.height == WindowType.Medium) 35.dp else 25.dp)
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(icons[index].url)

                    Log.d(TAG, "select icon = ${icons[index].url}")
                },
                selectedContentColor = Color.Red,
                unselectedContentColor = Color.White,
                alwaysShowLabel = false
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0x000000, heightDp = 300)
@Composable
fun DefaultPreviewSidebarMenu() {
    //SidebarMenu(0)
}