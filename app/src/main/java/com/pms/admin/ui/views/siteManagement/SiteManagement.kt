package com.pms.admin.ui.views.siteManagement

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.response.*
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.common.CustomAlertDialog
import com.pms.admin.ui.component.common.DeleteDialogEditItem
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.SiteViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        Log.e(TAG, "SiteManagement LaunchedEffected")
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
                    EditMode(siteViewModel, it.weight, it.siteId, it.siteName) { url ->
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
    val weights = listOf(1F, 2.5F, 1.2F, 1.5F, 1.2F, 2F)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(3f)
            .horizontalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
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
                            weight = 9.4F
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
                    content(
                        EditModeData(
                            weight = weights[5],
                            siteId = site_id,
                            siteName = site_name
                        )
                    )
                }
            }
        }
    }
}

//작업 아이콘들
@Composable
fun RowScope.EditMode(
    viewModel: SiteViewModel,
    weight: Float,
    siteId: Int,
    siteName: String,
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
                    onNavigator("site_mpu_delete/$siteId/$siteName")
                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_add),
            contentDescription = "person_add",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("site_manager_add/$siteId/$siteName")
                })

        Spacer(modifier = Modifier.width(5.dp))
        Icon(painter = painterResource(id = R.drawable.person_remove),
            contentDescription = "person_remove",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("site_manager_delete/$siteId/$siteName")
                })
        Spacer(modifier = Modifier.width(5.dp))
        Icon(imageVector = Icons.Outlined.Delete,
            contentDescription = "user delete",
            tint = Color.Red,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    deleteDialog = true
                })


        if (deleteDialog) {
            viewModel.setListUpdate(false)

            CustomAlertDialog(onDismissRequest = { deleteDialog = false }) {
                Column(
                    modifier = Modifier
                        .width(if (window.height == WindowType.Medium) 600.dp else 800.dp)
                        .height(630.dp)
                        .background(Color.LightGray)
                ) {
                    DeleteSite(
                        siteViewModel = viewModel,
                        siteId = siteId,
                        onDismissRequest = { deleteDialog = false }
                    )
                }

            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DeleteSite(
    siteViewModel: SiteViewModel,
    siteId: Int,
    viewModel: MainViewModel = viewModel(factory = PMSAndroidViewModelFactory(PMSAdminApplication.getInstance())),
    onDismissRequest: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    // var siteId by remember { mutableStateOf("") }
    var siteName by remember { mutableStateOf("") }
    var mpuId by remember { mutableStateOf("") }
    var mgrId by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current   //키보드 감추기
    val pref: SharedPreferences =
        context.getSharedPreferences("com.pms.admin.session_manager.SESSION_PREFERENCES", 0);
    val adminID = pref.getString("com.pms.admin.session_manager.SESSION_USER_ID", "") ?: " "

    val scrollState = rememberScrollState()  // mobile vertical scroll

    LaunchedEffect(siteId) {
        siteViewModel.getDeleteSites(siteId)
        siteViewModel.siteDeleteInfo.collect { site ->
            siteName = site.site_name
            mpuId = site.mpuid.joinToString(','.toString())
            mgrId = site.mgrid.joinToString(','.toString())
        }
    }

    LaunchedEffect(true) {
        siteViewModel.result.collect { result ->
            val message = if (result) String.format(
                context.resources.getString(R.string.site_success),
                "삭제"
            ) else String.format(
                context.resources.getString(R.string.site_fail),
                "삭제"
            )
            val job = scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(message)
            }
            delay(1000)
            job.cancel()

            onDismissRequest()

            if (result) siteViewModel.setListUpdate(true)
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ContentsBackground)
                .padding(30.dp)
                .verticalScroll(scrollState),
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

                Text(buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                        )
                    ) {
                        append("해당 사이트를 ")
                    }

                    withStyle(
                        SpanStyle(
                            color = Color.Red,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                        )
                    ) {
                        append("삭제 ")
                    }

                    withStyle(
                        SpanStyle(
                            color = Color.White,
                            fontSize = MaterialTheme.typography.body1.fontSize,
                        )
                    ) {
                        append("하시겠어요?")
                    }
                })
                Spacer(Modifier.height(5.dp))

                DeleteDialogEditItem(title = stringResource(id = R.string.id), siteId.toString())
                DeleteDialogEditItem(title = stringResource(id = R.string.name), siteName)
                DeleteDialogEditItem(title = stringResource(id = R.string.mpu), mpuId)
                DeleteDialogEditItem(title = stringResource(id = R.string.manager), mgrId)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                ) {
                    Text(
                        text = "*",
                        color = Color.Red,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.want_to_input_admin_password),
                        color = Color.White,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier.padding(5.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp, end = 30.dp),
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = ContentLine,
                                shape = RoundedCornerShape(5.dp)
                            ),
                        value = password,
                        maxLines = 1,
                        onValueChange = { password = it },
                        visualTransformation = PasswordVisualTransformation(),          //비밀번호 *로 표시되도록 처리
                        textStyle = TextStyle(
                            color = Color.White,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()

                            scope.launch {
                                if (password.isNotEmpty()) {
                                    viewModel.checkAdminPassword(
                                        adminID,
                                        computeSHAHash(password)
                                    )

                                    viewModel.checkAdminPasswordResult.collect { result ->
                                        if (!result) {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                context.resources.getString(
                                                    R.string.not_match_admin_password
                                                )
                                            )
                                        } else {
                                            siteViewModel.deleteSite(siteId, siteName)


                                        }
                                    }
                                } else {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        context.resources.getString(
                                            R.string.want_to_input_admin_password
                                        )
                                    )
                                }


                            }
                        }
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = ContentsBackground,
                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                        ),
                    )
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ContentLine)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                if (password.isNotEmpty()) {
                                    viewModel.checkAdminPassword(
                                        adminID,
                                        computeSHAHash(password)
                                    )

                                    viewModel.checkAdminPasswordResult.collect { result ->
                                        if (!result) {
                                            scaffoldState.snackbarHostState.showSnackbar(
                                                context.resources.getString(
                                                    R.string.not_match_admin_password
                                                )
                                            )
                                        } else {
                                            siteViewModel.deleteSite(siteId, siteName)


                                        }
                                    }
                                } else {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        context.resources.getString(
                                            R.string.want_to_input_admin_password
                                        )
                                    )
                                }


                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = onDismissRequest,
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