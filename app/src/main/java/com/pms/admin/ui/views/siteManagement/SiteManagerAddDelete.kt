package com.pms.admin.ui.views.siteManagement

import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.data.Mode
import com.pms.admin.model.response.*
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.SiteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private data class ManagerCheckItem(
    var id: String,                     //manager id
    var checked: Boolean = false        //manager checked
)

@Composable
fun SiteManagerAddDelete(
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
    val viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = rememberWindowSize()
    var managerList by remember { mutableStateOf<List<SiteManagerListResult>>(emptyList()) }
    var managerCheckedList by remember {
        mutableStateOf<Array<ManagerCheckItem>>(emptyArray())
    }

    LaunchedEffect(true) {
        siteViewModel.getManagerList(mode, siteId)
        siteViewModel.managerList.collect { list ->
            managerList = list
            managerCheckedList = Array(list.size) { ManagerCheckItem(id = "") }

            list.forEachIndexed { index, item ->
                managerCheckedList[index] = ManagerCheckItem(id = item.user_id, checked = false)
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    )
    {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(ContentsBackground)
                .padding(it)
        ) {
            SidebarMenu(navController, 1)

            Column(modifier = Modifier.fillMaxSize()) {
                Header(
                    navController,
                    viewModel,
                    stringResource(id = R.string.manager_add),
                    R.drawable.person_add
                )

                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                        .padding(top = 10.dp)
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
                    SiteManagerList(dataList = managerList) {
                        CheckManager(it.weight, it.userId) { id, checked ->
                            val index =
                                managerCheckedList.indexOf(ManagerCheckItem(it.userId))  //check된 mpu index를 받아옴
                            managerCheckedList[index].id = id
                            managerCheckedList[index].checked = checked
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
                            val managerList = JSONArray()
                            var validChecked = false

                            managerCheckedList.forEach { it ->
                                val jsonObject = JSONObject()
                                if (it.checked) {
                                    validChecked = true
                                    jsonObject.put("item", it.id)
                                }
                                managerList.put(jsonObject)
                            }

                            scope.launch {
                                if(!validChecked){
                                    val job = scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = String.format(context.resources.getString(R.string.no_selected_item),"관리자"),
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                    delay(1000)
                                    job.cancel()
                                    return@launch
                                }
                                siteViewModel.setSitesManagerAddDelete(
                                    mode,
                                    siteId,
                                    siteName,
                                    managerList
                                )

                                siteViewModel.result.collect { response ->
                                    val result = if (mode == Mode.Add) "추가" else "삭제"
                                    val message = if (response) String.format(
                                        context.resources.getString(
                                            R.string.site_manager_success
                                        ), result
                                    ) else
                                        String.format(
                                            context.resources.getString(
                                                R.string.site_manager_fail
                                            ), result
                                        )

                                    val job = scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = message,
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                    delay(1000)
                                    job.cancel()

                                    navController.popBackStack()
                                }
                            }

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
                        onClick = { navController.popBackStack() },
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
}

data class ManagerCheckData(val weight: Float, val userId: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.SiteManagerList(
    dataList: List<SiteManagerListResult>,
    content: @Composable RowScope.(data: ManagerCheckData) -> Unit
) {

    val headers = listOf("관리자 ID", "관리자 이름", "전화번호", "Check")
    val weights = listOf(2F, 2F, 2F, 2F)
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
                val (name, user_id, tel) = it

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    TableCell(text = name, weight = weights[0])
                    TableCell(text = user_id, weight = weights[1])
                    TableCell(text = tel, weight = weights[2])
                    content(ManagerCheckData(weight = weights[3], userId = user_id))
                }
            }
        }
    }
}

//check icon
@Composable
fun RowScope.CheckManager(
    weight: Float,
    mpuId: String,
    onChecked: (String, Boolean) -> Unit
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