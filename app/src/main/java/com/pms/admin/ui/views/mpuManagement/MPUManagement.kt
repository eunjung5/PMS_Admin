package com.pms.admin.ui.views.mpuManagement

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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.*
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.response.*
import com.pms.admin.ui.component.common.CustomAlertDialog
import com.pms.admin.ui.component.common.DeleteDialogEditItem
import com.pms.admin.ui.component.common.showSnackBar
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.MPUViewModel
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.ManagerViewModel
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MPUManagement(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    mpuViewModel: MPUViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    )
) {
    val window = rememberWindowSize()
    var mpuList by remember{ mutableStateOf(emptyList<MPUListResult>()) }
    val update = mpuViewModel.updateList.value

    LaunchedEffect(true,update) {
        mpuViewModel.getMPUList()
        mpuViewModel.mpuList.collect{
           mpuList = it
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        SidebarMenu(navController, 2)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.mpu_management),
                R.drawable.menu_mpu
            )

            //mpu create button
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
                        onClick = { navController.navigate("mpu_add") },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MenuBackground),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.mpu_add), color = Color.White
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
                MPUManagementList(
                    dataList = mpuList,
                ) {
                    EditMode(mpuViewModel, it.weight, it.mpuId) { url ->
                        navController.navigate(url)
                    }
                }
            }
        }

    }
}

data class EditModeData(val weight: Float, val mpuId: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.MPUManagementList(
    dataList: List<MPUListResult>,
    content: @Composable RowScope.(data: EditModeData) -> Unit
) {

    val headers = listOf("MPU ID", "사이트", "구성", "작업")
    val weights = listOf(1F, 1.5F, 5F, 1F)
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
                            weight = 8.5F
                        )
                    }
                }
            }

            items(dataList) {
                val (mpu_id, site_name, composition) = it
                val compositionList = composition.joinToString(','.toString())


                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = mpu_id.toString(), weight = weights[0])
                    TableCell(text = site_name, weight = weights[1])
                    TableCell(text = compositionList, weight = weights[2])

                    content(
                        EditModeData(
                            weight = weights[3],
                            mpuId = mpu_id,
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
    viewModel: MPUViewModel,
    weight: Float,
    mpuId: Int,
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
            contentDescription = "mpu_edit",
            tint = Color.White,
            modifier = Modifier
                .width(20.dp)
                .clickable {
                    onNavigator("mpu_edit/$mpuId")
                })


      //  Spacer(modifier = Modifier.width(5.dp))
        Icon(imageVector = Icons.Outlined.Delete,
            contentDescription = "mpu_delete",
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
                        .height(480.dp)
                        .background(Color.LightGray)
                ) {
                    DeleteMPU(
                        mpuId = mpuId.toString(),
                        mpuViewModel = viewModel,
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
fun DeleteMPU(
    mpuId: String,
    mpuViewModel: MPUViewModel,
    viewModel: MainViewModel = viewModel(factory = PMSAndroidViewModelFactory(PMSAdminApplication.getInstance())),
    onDismissRequest: () -> Unit,
) {

    var password by remember { mutableStateOf("") }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current   //키보드 감추기
    val pref: SharedPreferences =
        context.getSharedPreferences("com.pms.admin.session_manager.SESSION_PREFERENCES", 0);
    val adminID = pref.getString("com.pms.admin.session_manager.SESSION_USER_ID", "") ?: " "

    val scrollState = rememberScrollState()  // mobile vertical scroll

    LaunchedEffect(true) {
        viewModel.checkAdminPasswordResult.collect { result ->
            if (!result) {
                showSnackBar(
                    scaffoldState = scaffoldState,
                    message = context.resources.getString(R.string.not_match_admin_password)
                )
            } else {
                mpuViewModel.deleteMPU(mpuId)
            }
        }
    }

    LaunchedEffect(true) {
        mpuViewModel.result.collect { result ->
            Log.e(TAG,"mpuViewModel result = $result")
            val message = if (result) String.format(
                context.resources.getString(R.string.delete_mpu_message),
                "성공"
            ) else String.format(context.resources.getString(R.string.delete_mpu_message), "실패")
            showSnackBar(scaffoldState = scaffoldState, message = message)
            //scaffoldState.snackbarHostState.showSnackbar(message)

            onDismissRequest()

            if (result) mpuViewModel.setListUpdate(true)
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
                        append("해당 MPU를 ")
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
                    text = stringResource(id = R.string.warning_mpu_delete_message),
                    color = Color.White,
                    fontSize = MaterialTheme.typography.body1.fontSize,
                )

                DeleteDialogEditItem(
                    title = stringResource(id = R.string.delete_mpu),
                    content = mpuId
                )

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
                                else {
                                    showSnackBar(
                                        scaffoldState = scaffoldState,
                                        message = context.resources.getString(
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
                                keyboardController?.hide()
                                if (password.isNotEmpty())
                                    viewModel.checkAdminPassword(
                                        adminID,
                                        computeSHAHash(password)
                                    )
                                else {
                                    showSnackBar(
                                        scaffoldState = scaffoldState,
                                        message =  context.resources.getString(
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