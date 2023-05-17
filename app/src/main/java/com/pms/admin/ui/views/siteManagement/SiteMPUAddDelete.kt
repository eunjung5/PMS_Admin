package com.pms.admin.ui.views.siteManagement

import android.util.Log
import android.widget.CheckBox
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.Mode
import com.pms.admin.model.SiteMPUListResult
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.SiteViewModel
import com.pms.admin.ui.views.managerManagement.CustomAlertDialog
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.launch

@Composable
fun SiteMPUAddDelete(
    navController: NavHostController,
    siteId: String,
    siteName: String,
    mode: Mode,
    siteViewModel: SiteViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    ),
) {

    Log.e(TAG, "siteId = $siteId, siteName=$siteName")
    val viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = rememberWindowSize()
    var mpuList by remember { mutableStateOf<List<SiteMPUListResult>>(emptyList()) }

    LaunchedEffect(true) {
        siteViewModel.getMPUList(mode, siteId)
        siteViewModel.mpuList.collect {
            mpuList = it
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        SidebarMenu(navController, 1)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.mpu_add),
                R.drawable.add_circle
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
                        .padding(
                            start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                            end = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                        ),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontSize = MaterialTheme.typography.body2.fontSize,
                            )
                        ) {
                            append(stringResource(R.string.site_id) + " ")
                        }

                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontSize = MaterialTheme.typography.h6.fontSize,
                            )
                        ) {
                            append(siteId)
                        }
                    })

                    Text(buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontSize = MaterialTheme.typography.body2.fontSize,
                            )
                        ) {
                            append(stringResource(R.string.site_name) + " ")
                        }

                        withStyle(
                            SpanStyle(
                                color = Color.White,
                                fontSize = MaterialTheme.typography.h6.fontSize,
                            )
                        ) {
                            append(siteName)
                        }
                    })
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(if (window.height == WindowType.Medium) 5f else 3f)
                    .padding(
                        start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                        end = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                SiteMPUList(dataList = mpuList) {
                    CheckMPU( it.weight, it.mpuId) {id,checked->
                            Log.e(TAG,"checked mpu =${it.mpuId} =  ${id}/${checked}")
                    }
                }


            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(
                        start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                        end = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                    ),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(id = R.string.store))
                }
                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            }

        }

    }

}

data class MPUCheckData(val weight: Float, val mpuId: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.SiteMPUList(
    dataList: List<SiteMPUListResult>,
    content: @Composable RowScope.(data: MPUCheckData) -> Unit
) {

    val headers = listOf("MPU ID", "Check")
    val weights = listOf(4F, 4F)
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
                            weight = 8F
                        )
                    }
                }
            }

            items(dataList) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = it.mpuid, weight = weights[0])
                    content(MPUCheckData(weight = weights[1], mpuId = it.mpuid))
                }
            }
        }
    }
}

//check icon
@Composable
fun RowScope.CheckMPU(
    weight: Float,
    mpuId: String,
    onChecked: (String,Boolean) -> Unit
) {

    var checked by remember { mutableStateOf(false) }

    val width = weight * 100
    return Row(
        modifier = Modifier
            .width(width.dp)
            .height(50.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF5A5F62))
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Checkbox(
            checked = checked,
            enabled = true,
            onCheckedChange = { it ->
                checked = it
                onChecked(mpuId, it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Gray,
                uncheckedColor = Color.White,
            )
        )
    }
}