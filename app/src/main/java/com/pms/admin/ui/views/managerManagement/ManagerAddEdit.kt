package com.pms.admin.ui.views.managerManagement

import android.content.Context
import android.text.Editable.Factory
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.Mode
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.component.common.RegisterItem
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.viewModels.ManagerViewModel
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FocusItem(val currentFocus: FocusRequester, val nextFocus: FocusRequester?)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManagerAddEdit(
    navController: NavHostController,
    userId: String,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    managerViewModel: ManagerViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    )
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tel by remember { mutableStateOf("") }
    val radioOptions = listOf("관리자L1", "관리자L2", "개발자")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기

    val focusManager = LocalFocusManager.current
    val (idFocus, nameFocus, passwordFocus, telFocus) = FocusRequester.createRefs()     //next text focus

    val window = rememberWindowSize()
    val mode: Mode = if (userId.isEmpty()) Mode.Add else Mode.Edit

    var checkDuplicationID by remember { mutableStateOf(userId.isNotEmpty()) }
    val context = LocalContext.current
    Log.e(TAG, " init checkDuplicationID = $checkDuplicationID")
    //사용자 등록 및 수정 성공,실패 팝업창
    LaunchedEffect(true) {
        managerViewModel.registerUserResult.collect { response ->
            val result =
                if (mode == Mode.Add) context.resources.getString(R.string.register) else context.resources.getString(
                    R.string.modify
                )

            if (response) {
                val job = scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = String.format(
                            context.resources.getString(R.string.user_register_success),
                            result
                        ),
                        duration = SnackbarDuration.Indefinite
                    )
                }
                delay(1000)
                job.cancel()

                navController.popBackStack()
            } else {
                scaffoldState.snackbarHostState.showSnackbar(
                    String.format(
                        context.resources.getString(R.string.user_register_fail),
                        result
                    )
                )
            }
        }
    }

    //사용자 정보 불러오기
    LaunchedEffect(userId) {
        managerViewModel.getUserInfo(userId)

        managerViewModel.userInfo.collect { user ->
            onOptionSelected(getRoleString(context, user.role))
            id = user.user_id
            name = user.name
            tel = user.tel
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
                .background(AdminBackground)
                .padding(it)
        ) {
            SidebarMenu(navController, 0)

            Column(modifier = Modifier.fillMaxSize()) {
                if (mode == Mode.Add)
                    Header(
                        navController,
                        viewModel,
                        stringResource(R.string.manager_create),
                        R.drawable.person_add
                    )
                else
                    Header(
                        navController,
                        viewModel,
                        stringResource(R.string.manager_modify),
                        R.drawable.person_add
                    ) //아이콘 차후 수정

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(4f)
                        .padding(
                            start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                            end = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                            bottom = 30.dp
                        )
                        .background(color = ContentsBackground, shape = RoundedCornerShape(30.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(
                                start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                                end = 10.dp,
                                top = if (window.height == WindowType.Medium) 30.dp else 0.dp
                            ),
                        contentPadding = PaddingValues(10.dp),
                    ) {
                        //id text
                        item {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 30.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(4f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(top = 30.dp)
                                    ) {
                                        if (mode == Mode.Add) {
                                            Text(text = "*", color = Color.Red)
                                        }

                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = "ID",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(5f)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            TextField(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(
                                                        width = 1.dp,
                                                        color = ContentLine,
                                                        shape = RoundedCornerShape(5.dp)
                                                    )
                                                    .focusRequester(idFocus)
                                                    .focusProperties {
                                                        next = nameFocus
                                                    },
                                                readOnly = mode == Mode.Edit,
                                                value = id,
                                                maxLines = 1,
                                                onValueChange = { id = it },
                                                shape = RoundedCornerShape(5.dp),
                                                textStyle = TextStyle(
                                                    color = if (mode == Mode.Edit) Color.Gray else Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    textDecoration = TextDecoration.None,
                                                ),
                                                colors = TextFieldDefaults.textFieldColors(
                                                    backgroundColor = ContentsBackground,
                                                    focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    cursorColor = Color.White,
                                                ),
                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(onNext = {
                                                    focusManager.moveFocus(FocusDirection.Next)
                                                }),
                                            )
                                            Spacer(
                                                Modifier
                                                    .height(5.dp)
                                                    .width(5.dp)
                                            )
                                            if (mode == Mode.Add) Text(
                                                text = stringResource(R.string.id_description),
                                                color = Color.LightGray
                                            )
                                        }
                                    }

                                }

                                if (mode == Mode.Add) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(top = 10.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Transparent,
                                                shape = RoundedCornerShape(30.dp)
                                            ),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                            onClick = {
                                                keyboardController?.hide()
                                                managerViewModel.checkDuplicatedId(id)

                                                //아이디 중복 체크
                                                scope.launch {
                                                    managerViewModel.checkDuplicatedId.collect { duplicated ->
                                                        val text =
                                                            if (duplicated) String.format(
                                                                context.resources.getString(R.string.duplicate_fail),
                                                                "아이디"
                                                            ) else String.format(
                                                                context.resources.getString(R.string.duplicate_success),
                                                                "아이디"
                                                            )
                                                        checkDuplicationID = !duplicated
                                                        scaffoldState.snackbarHostState.showSnackbar(
                                                            text
                                                        )
                                                    }
                                                }

                                            }) {
                                            Text(
                                                text = stringResource(id = R.string.duplicate_check),
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {}
                                }


                            }
                        }

                        //name text
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.name),
                                item = name,
                                assistance = stringResource(id = R.string.name_description),
                                required = (mode == Mode.Add),
                                focusItem = FocusItem(
                                    nameFocus,
                                    if (mode == Mode.Add) passwordFocus else telFocus
                                ),
                                onTextValueChange = { name = it }
                            )
                        }

                        //level radio button
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 30.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(4f)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .padding(top = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (mode == Mode.Add) {
                                            Text(text = "*", color = Color.Red)
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = stringResource(id = R.string.level),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(5f)
                                            .padding(10.dp)
                                            .selectableGroup()
                                    ) {
                                        radioOptions.forEach { text ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .weight(4f)
                                                    .selectable(
                                                        selected = (text == selectedOption),
                                                        onClick = { onOptionSelected(text) },
                                                        role = Role.RadioButton
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (text == selectedOption),
                                                    onClick = null,
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = Color.LightGray,
                                                        unselectedColor = Color.White
                                                    )
                                                )
                                                Text(
                                                    text = text,
                                                    modifier = Modifier.padding(start = 5.dp),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                //중복 확인 버튼 만큼 공간 비우기
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {}

                            }
                        }
                        if (mode == Mode.Add) {
                            //password text
                            item {
                                RegisterItem(
                                    title = stringResource(id = R.string.password),
                                    item = password,
                                    assistance = "",
                                    required = true,
                                    focusItem = FocusItem(passwordFocus, telFocus),
                                    onTextValueChange = { password = it }
                                )
                            }
                        }


                        //tel text
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.telephone),
                                item = tel,
                                assistance = "",
                                required = false,
                                focusItem = FocusItem(telFocus, null),
                                onDone = {
                                    keyboardController?.hide()
                                    var message: String = ""

                                    if (checkDuplicationID) {
                                        if (id.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
                                            if (mode == Mode.Add) {
                                                managerViewModel.registerUser(
                                                    id,
                                                    getRoleString(context, selectedOption),
                                                    name,
                                                    computeSHAHash(password),
                                                    tel
                                                )
                                            } else {
                                                managerViewModel.updateUser(
                                                    id,
                                                    getRoleString(context, selectedOption),
                                                    name,
                                                    tel
                                                )
                                            }
                                        } else {
                                            message =
                                                context.resources.getString(R.string.required_msg)
                                        }

                                    } else {
                                        message = String.format(
                                            context.resources.getString(
                                                R.string.duplicate_confirm,
                                                "아이디"
                                            )
                                        )

                                    }
                                    if (message.isNotEmpty())
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(message)
                                        }
                                },
                                onTextValueChange = { tel = it }
                            )

                        }

                        //저장 and 취소 buttons
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 30.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    modifier = Modifier.border(
                                        width = 1.dp,
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                                    onClick = {

                                        var message: String = ""
                                        if (checkDuplicationID) {
                                            if (id.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()) {
                                                if (mode == Mode.Add) {
                                                    managerViewModel.registerUser(
                                                        id,
                                                        getRoleString(context, selectedOption),
                                                        name,
                                                        computeSHAHash(password),
                                                        tel
                                                    )
                                                } else {
                                                    managerViewModel.updateUser(
                                                        id,
                                                        getRoleString(context, selectedOption),
                                                        name,
                                                        tel
                                                    )
                                                }
                                            } else {
                                                message =
                                                    context.resources.getString(R.string.required_msg)
                                            }

                                        } else {
                                            message = String.format(
                                                context.resources.getString(
                                                    R.string.duplicate_confirm,
                                                    "아이디"
                                                )
                                            )

                                        }
                                        if (message.isNotEmpty())
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(message)
                                            }

                                    }) {
                                    Text(text = stringResource(R.string.store), color = Color.White)
                                }

                                Spacer(Modifier.width(10.dp))

                                Button(
                                    modifier = Modifier.border(
                                        width = 1.dp,
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(5.dp)
                                    ),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                    onClick = {
                                        navController.popBackStack()
                                    }) {
                                    Text(
                                        text = stringResource(R.string.cancel),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getRoleString(context: Context, level: String): String {
    return when (level) {
        context.resources.getString(R.string.manager1) -> context.resources.getString(R.string.manager1_eng)
        context.resources.getString(R.string.manager2) -> context.resources.getString(R.string.manager2_eng)
        context.resources.getString(R.string.developer) -> context.resources.getString(R.string.developer_eng)
        context.resources.getString(R.string.manager1_eng) -> context.resources.getString(R.string.manager1)
        context.resources.getString(R.string.manager2_eng) -> context.resources.getString(R.string.manager2)
        context.resources.getString(R.string.developer_eng) -> context.resources.getString(R.string.developer)
        else -> ""
    }

}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
fun ManagerAddPreview() {
    val radioOptions = listOf("관리자L1", "관리자L2", "개발자")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(30.dp))
            .background(ContentsBackground)
            .padding(30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 30.dp)
            ) {
                Text(text = "*", color = Color.Red)
                Spacer(Modifier.width(10.dp))
                Text(text = "Level", color = Color.White, fontWeight = FontWeight.Bold)
            }


            Row(
                modifier = Modifier
                    .weight(4f)
                    .padding(10.dp)
                    .selectableGroup()
            ) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .weight(4f)
                            // .height(56.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            ),
                        //.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = text,
                            //style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(start = 5.dp),
                            color = Color.White
                        )
                    }
                }
            }


        }
    }
}