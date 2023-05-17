package com.pms.admin.ui.views.managerManagement

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.*
import com.pms.admin.R
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.ManagerListResult
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.*
import com.pms.admin.ui.viewModels.ManagerViewModel
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ManagerManagement(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    managerViewModel: ManagerViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    )
) {
    var managerList = managerViewModel.managerList
    val window = rememberWindowSize()
    val update = managerViewModel.updateList.value

    LaunchedEffect(true, update) {
        managerViewModel.getManagerList()
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SidebarMenu(navController, 0)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.manager_management),
                R.drawable.menu_manager
            )

            //manager create button
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
                        onClick = { navController.navigate("manager_add") },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MenuBackground),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.manager_create),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "manager_create",
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
                ManagerManagementList(
                    dataList = managerList.value,
                ) {
                    EditMode(managerViewModel, it.weight, it.userId) { url ->
                        navController.navigate(url)
                    }
                }
            }
        }

    }

}

data class EditModeData(val weight: Float, val userId: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.ManagerManagementList(
    dataList: List<ManagerListResult>,
    content: @Composable RowScope.(data: EditModeData) -> Unit
) {

    val headers = listOf("ID", "이름", "레벨", "전화번호", "사이트", "작업")
    val weights = listOf(1.5F, 1F, 1.5F, 1.5F, 2F, 2F)
    val scrollState = rememberScrollState()

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
                        TableCell(text = stringResource(id = R.string.no_content), weight = 9.5F)
                    }
                }
            }

            items(dataList) {
                val (descr, name, role, sites, tel, user_id) = it
                val site = sites.joinToString()

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = user_id, weight = weights[0])
                    TableCell(text = name, weight = weights[1])
                    TableCell(text = role, weight = weights[2])
                    TableCell(text = tel, weight = weights[3])
                    TableCell(text = site, weight = weights[4])
                    content(EditModeData(weight = weights[5], userId = user_id))
                }
            }
        }
    }
}

//작업 아이콘들
@Composable
fun RowScope.EditMode(
    viewModel: ManagerViewModel,
    weight: Float,
    userId: String,
    onNavigator: (String) -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    val window = rememberWindowSize()
    val width = weight * 100

    return Row(
        modifier = Modifier
            // .weight(weight)
            .width(width.dp)
            .height(50.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFF5A5F62))
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = "user edit",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("manager_edit/$userId")
                }
        )

        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = "user password reset",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("manager_password_edit/$userId")
                }
        )

        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "user search",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("manager_job_search/$userId")
                }
        )

        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "user delete",
            tint = Color.Red,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    deleteDialog = true
                }
        )

        if (deleteDialog) {
            CustomAlertDialog(onDismissRequest = { deleteDialog = false }) {
                Column(
                    modifier = Modifier
                        .width(if (window.height == WindowType.Medium) 600.dp else 800.dp)
                        .height(550.dp)
                        .background(Color.LightGray)
                ) {
                    DeleteUser(
                        viewModel = viewModel,
                        userId = userId
                    ) {
                        deleteDialog = false

                    }
                }

            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DeleteUser(
    viewModel: ManagerViewModel,
    userId: String,
    onDismissRequest: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current   //키보드 감추기
    val pref: SharedPreferences =
        context.getSharedPreferences("com.pms.admin.session_manager.SESSION_PREFERENCES", 0);
    val adminID = pref.getString("com.pms.admin.session_manager.SESSION_USER_ID", "") ?: " "

    val scrollState = rememberScrollState()  // mobile vertical scroll

    LaunchedEffect(userId) {
        viewModel.getUserInfo(userId)
        viewModel.userInfo.collect { user ->
            id = user.user_id
            name = user.name
        }
    }

    LaunchedEffect(true) {
        viewModel.checkAdminPasswordResult.collect { result ->
            if (!result) {
                scaffoldState.snackbarHostState.showSnackbar(context.resources.getString(R.string.not_match_admin_password))
            } else {
                viewModel.deleteUser(userId)
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.result.collect { result ->
            val message = if (result) String.format(
                context.resources.getString(R.string.delete_user_message),
                "성공"
            ) else String.format(context.resources.getString(R.string.delete_user_message), "실패")
            scaffoldState.snackbarHostState.showSnackbar(message)
            onDismissRequest()

            if (result) viewModel.setListUpdate(true)
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
                        append("해당 관리자를 ")
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
                Text(
                    text = stringResource(id = R.string.warning_user_delete_message),
                    color = Color.White,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.id),
                        color = Color.White,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier.padding(5.dp)
                    )

                    Spacer(Modifier.width(43.dp))

                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier
                            .width(300.dp)
                            .background(DisableEditBackground, shape = RoundedCornerShape(5.dp))
                            .padding(10.dp)

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.name),
                        color = Color.White,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier.padding(5.dp)
                    )

                    Spacer(Modifier.width(30.dp))

                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = MaterialTheme.typography.body1.fontSize,
                        modifier = Modifier
                            .width(300.dp)
                            .background(DisableEditBackground, shape = RoundedCornerShape(5.dp))
                            .padding(10.dp)
                    )
                }

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

                                if (password.isNotEmpty())
                                    viewModel.checkAdminPassword(
                                        adminID,
                                        computeSHAHash(password)
                                    )
                                else
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        context.resources.getString(
                                            R.string.want_to_input_admin_password
                                        )
                                    )
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
                                if (password.isNotEmpty())
                                    viewModel.checkAdminPassword(
                                        adminID,
                                        computeSHAHash(password)
                                    )
                                else
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        context.resources.getString(
                                            R.string.want_to_input_admin_password
                                        )
                                    )
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

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        content()
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000,
    device = "spec:width=600dp,height=500dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun DefaultPreviewSidebarMenu() {

}