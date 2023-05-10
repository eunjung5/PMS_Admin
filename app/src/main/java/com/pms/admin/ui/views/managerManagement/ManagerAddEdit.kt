package com.pms.admin.ui.views.managerManagement

import android.util.Log
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.model.Mode
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.MainViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.util.computeSHAHash

data class FocusItem(val currentFocus: FocusRequester, val nextFocus: FocusRequester?)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManagerAddEdit(
    navController: NavHostController,
    viewModel: MainViewModel,
    userId: String,
) {
    val scaffoldState = rememberScaffoldState()
    var id by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var tel by rememberSaveable { mutableStateOf("") }
    val radioOptions = listOf("관리자L1", "관리자L2", "개발자")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기

    val focusManager = LocalFocusManager.current
    val (idFocus, nameFocus, passwordFocus, telFocus) = FocusRequester.createRefs()     //next text focus

    val window = rememberWindowSize()
    val mode: Mode = if (userId.isEmpty()) Mode.Add else Mode.Edit

    //아이디 중복 체크
    LaunchedEffect(key1 = true) {
        viewModel.checkDuplicatedId.collect { checkDuplicatedId ->
            if (checkDuplicatedId)
                scaffoldState.snackbarHostState.showSnackbar("중복된 아이디입니다.")
            else
                scaffoldState.snackbarHostState.showSnackbar("사용 가능한 아이디입니다.")
        }
    }

    //사용자 등록 및 수정 성공,실패 팝업창
    LaunchedEffect(key1 = true) {
        viewModel.registerUserResult.collect { result ->
            if (result){
                if(mode == Mode.Add)
                    scaffoldState.snackbarHostState.showSnackbar("사용자가 등록 성공입니다.")
                else 
                    scaffoldState.snackbarHostState.showSnackbar("사용자가 수정 성공입니다.")
                navController.popBackStack()
            }
            else{
                if(mode == Mode.Add)
                    scaffoldState.snackbarHostState.showSnackbar("사용자가 등록 실패입니다.")
                else
                    scaffoldState.snackbarHostState.showSnackbar("사용자가 수정 실패입니다.")
            }

        }
    }

    //사용자 정보 불러오기
    LaunchedEffect(key1 = userId) {
        viewModel.getUserInfo(userId)

        viewModel.userInfo.collect { user ->
            val role = when (user.role) {
                "manager1" -> "관리자L1"
                "manager2" -> "관리자L2"
                else -> "개발자"
            }
            onOptionSelected(role)
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
                    Header(navController, viewModel, "관리자 생성", R.drawable.person_add)
                else
                    Header(navController, viewModel, "관리자 수정", R.drawable.person_add) //아이콘 차후 수정
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(4f)
                        .padding(
                            start = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                            end = if (window.height == WindowType.Medium) 50.dp else 30.dp,
                            bottom = if (window.height == WindowType.Medium) 50.dp else 30.dp
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
                                                text = "영문/숫자 혼용 5글자 이상 입력해주세요.",
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
                                                viewModel.checkDuplicatedId(id)
                                            }) {
                                            Text(text = "중복확인", color = Color.White)
                                        }
                                    }
                                }
                                else{
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
                            RegisterUserItem(
                                title = "이름",
                                item = name,
                                assistance = "2 ~ 10 글자 안으로 입력해주세요.",
                                required = (mode == Mode.Add),
                                focusItem = FocusItem(
                                    nameFocus,
                                    if (mode == Mode.Add) passwordFocus else telFocus
                                ),
                                onTextValueChange = {name = it}
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
                                            text = "Level",
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
                                                    onClick = null,
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = Color.LightGray,
                                                        unselectedColor = Color.White
                                                    )
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
                                RegisterUserItem(
                                    title = "Password",
                                    item = password,
                                    assistance = "",
                                    required = true,
                                    focusItem = FocusItem(passwordFocus, telFocus),
                                    onTextValueChange = {password = it}
                                )
                            }
                        }


                        //tel text
                        item {
                            RegisterUserItem(
                                title = "전화번호",
                                item =  tel,
                                assistance = "",
                                required = false,
                                focusItem = FocusItem(telFocus, null),
                                onDone = {
                                    keyboardController?.hide()

                                    val role = when (selectedOption) {
                                        "관리자L1" -> "manager1"
                                        "관리자L2" -> "manager2"
                                        else -> "developer"
                                    }
                                    viewModel.registerUser(
                                        id,
                                        role,
                                        name,
                                        computeSHAHash(password),
                                        tel
                                    )
                                },
                                onTextValueChange = {tel = it}
                            )

                        }

                        //저장 and 취소 buttons
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                        val role = when (selectedOption) {
                                            "관리자L1" -> "manager1"
                                            "관리자L2" -> "manager2"
                                            else -> "developer"
                                        }
                                        Log.e(TAG, "name = $name, tel = $tel")
                                        if(mode == Mode.Add)
                                        {
                                            viewModel.registerUser(
                                                id,
                                                role,
                                                name,
                                                computeSHAHash(password),
                                                tel
                                            )
                                        }
                                        else{
                                            viewModel.updateUser(
                                                id,
                                                role,
                                                name,
                                                tel
                                            )
                                        }


                                    }) {
                                    Text(text = "저장", color = Color.White)
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
                                    Text(text = "취소", color = Color.White)
                                }
                            }
                        }
                    }

                }


            }

        }
    }

}

@Composable
fun RegisterUserItem(
    title: String,
    item: String,
    assistance: String,
    required: Boolean,
    focusItem: FocusItem,
    onDone: (() -> Unit)? = null,
    onTextValueChange: (String) -> Unit,
) {
    var content by remember { mutableStateOf(item) }
    Log.d(TAG, "$title = $item, content = $content")
    val focusManager = LocalFocusManager.current

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
                    .padding(top = 30.dp),
            ) {
                if (required){
                    Text(text = "*" , color = Color.Red)
                }

                Spacer(Modifier.width(10.dp))
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f)
                    .width(100.dp)
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
                        .focusRequester(focusItem.currentFocus)
                        .focusProperties {
                            focusItem.nextFocus.let {
                                if (it != null) {
                                    next = it
                                }
                            }
                        },
                    value = item,
                    maxLines = 1,
                    onValueChange = onTextValueChange,
                    shape = RoundedCornerShape(5.dp),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    visualTransformation = if (title === "Password") PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = ContentsBackground,
                        focusedIndicatorColor = Color.Transparent,   //hide the indicator
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                    ),

                    keyboardOptions = if (title == "전화번호") KeyboardOptions.Default.copy(imeAction = ImeAction.Done) else KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {

                        focusManager.moveFocus(FocusDirection.Next)
                    },
                        onDone = {
                            if (onDone != null) {
                                onDone()
                            }
                        }),
                )
                Spacer(
                    Modifier
                        .height(5.dp)
                        .width(5.dp)
                )
                if (assistance.isNotEmpty()) Text(
                    text = assistance, color = Color.LightGray,
                    modifier = Modifier.padding(start = 10.dp)
                )
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