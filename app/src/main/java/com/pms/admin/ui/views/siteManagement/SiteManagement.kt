package com.pms.admin.ui.views.siteManagement

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.SiteListResult
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.SiteViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.views.managerManagement.CustomAlertDialog


@Composable
fun SiteManagement(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    siteViewModel: SiteViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    )
) {

    val window = rememberWindowSize()
    val update = siteViewModel.updateList.value
    var siteList = siteViewModel.siteList

    LaunchedEffect(true, update) {
        siteViewModel.getSiteList()
    }



    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        SidebarMenu(navController, 1)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.site_management),
                R.drawable.menu_site
            )

            //site create button
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Button(
                        onClick = { navController.navigate("site_add") },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MenuBackground),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.site_create), color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "site_create",
                            tint = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(if (window.height == WindowType.Medium) 5f else 3f)
            ) {
                SiteManagementList(
                    dataList = siteList.value,
                ) {
                    EditMode(viewModel, it.weight, it.siteId,it.siteName) { url ->
                        navController.navigate(url)
                    }
                }
            }
        }

    }

}

data class EditModeData(val weight: Float, val siteId: Int, val siteName: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.SiteManagementList(
    dataList: List<SiteListResult>,
    content: @Composable RowScope.(data: EditModeData) -> Unit
) {

    val headers = listOf("사이트", "주소", "MPU", "관리자", "고객", "작업")
    val weights = listOf(1F, 3F, 1.2F, 1.5F, 1.5F, 2F)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(3f)
            .horizontalScroll(scrollState)
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            stickyHeader {
                Row(Modifier.background(MenuBackground)) {
                    for (i in headers.indices) {
                        TableCell(text = headers[i], weight = weights[i])
                    }

                }
            }

            if (dataList.isEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth()) {
                        TableCell(
                            text = context.resources.getString(R.string.no_content),
                            weight = 10.2F
                        )
                    }
                }
            }

            items(dataList) {
                val (descr, mgrid, mpuid, site_addr, site_id, site_name, user_id) = it
                val mpuList = mpuid.joinToString(','.toString())
                val mgrList = mgrid.joinToString(','.toString())
                val userList = user_id.joinToString(','.toString())

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = site_name, weight = weights[0])
                    TableCell(text = site_addr, weight = weights[1])
                    TableCell(text = mpuList, weight = weights[2])
                    TableCell(text = mgrList, weight = weights[3])
                    TableCell(text = userList, weight = weights[4])
                    content(EditModeData(weight = weights[5], siteId = site_id,siteName=site_name))
                }
            }
        }
    }
}

//작업 아이콘들
@Composable
fun RowScope.EditMode(
    viewModel: MainViewModel,
    weight: Float,
    siteId: Int,
    siteName:String,
    onNavigator: (String) -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    val window = rememberWindowSize()
    val width = weight * 100
    return Row(
        modifier = Modifier
            //.weight(weight)
            .width(width.dp)
            .height(50.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF5A5F62))
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Icon(imageVector = Icons.Outlined.Edit,
            contentDescription = "site_edit",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("site_edit/$siteId")
                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.add_circle),
            contentDescription = "mpu_add",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("site_mpu_add/$siteId/$siteName")
                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.remove_circle),
            contentDescription = "mpu_remove",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_add),
            contentDescription = "person_add",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_remove),
            contentDescription = "person_remove",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })
        Spacer(modifier = Modifier.width(5.dp))
        Icon(imageVector = Icons.Outlined.Delete,
            contentDescription = "user delete",
            tint = Color.Red,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })


        if (deleteDialog) {
            CustomAlertDialog(onDismissRequest = { deleteDialog = false }) {
                Column(
                    modifier = Modifier
                        .width(if (window.height == WindowType.Medium) 600.dp else 800.dp)
                        .height(550.dp)
                        .background(Color.LightGray)
                ) {

                }

            }
        }

    }
}

@Preview
@Composable
fun SiteListPreview() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(imageVector = Icons.Outlined.Edit,
            contentDescription = "site_edit",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
//                    onNavigator("site_edit/$siteId")
                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.add_circle),
            contentDescription = "mpu_add",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.remove_circle),
            contentDescription = "mpu_remove",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_add),
            contentDescription = "person_add",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_remove),
            contentDescription = "person_remove",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })
        Spacer(modifier = Modifier.width(5.dp))
        Icon(imageVector = Icons.Outlined.Delete,
            contentDescription = "user delete",
            tint = Color.Red,
            modifier = Modifier
                .width(20.dp)
                .clickable {

                })


    }

}