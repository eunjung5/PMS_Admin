package com.pms.admin.ui.views.managerManagement

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.MainViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManagerPasswordEdit(
    navController: NavHostController,
    viewModel: MainViewModel,
    userId: String,
) {
    val scaffoldState = rememberScaffoldState()
    var id by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var checkPassword by rememberSaveable { mutableStateOf("") }
    var tel by rememberSaveable { mutableStateOf("") }
    val smsCheckedState = remember { mutableStateOf(true) }

    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기
    //next text focus
    val focusRequester = remember { FocusRequester() }
    val window = rememberWindowSize()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = userId) {
        viewModel.getUserInfo(userId)

        viewModel.userInfo.collect { user ->
            id = user.user_id
            name = user.name
            tel = user.tel
        }
    }

    LaunchedEffect(key1 = true) {

        viewModel.result.collect { result ->
            if (result){
                scaffoldState.snackbarHostState.showSnackbar( "비밀번호가 재설정 되었습니다."  )
                navController.popBackStack()
            }
            else{
                scaffoldState.snackbarHostState.showSnackbar( "비밀번호가 재설정이 실패 되었습니다.")
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
                .background(AdminBackground)
                .padding(it)
        ) {
            SidebarMenu(navController, 0)

            Column(modifier = Modifier.fillMaxSize()) {
                Header(navController, viewModel, "관리자 PW 변경", R.drawable.lock_reset)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(4f)
                        .padding(start = 30.dp, end = 30.dp, bottom = 30.dp)
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
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "ID",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                        .width(100.dp)
                                        .padding(10.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                color = ContentLine,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        readOnly = userId.isNotEmpty(),
                                        value = id,
                                        maxLines = 1,
                                        onValueChange = { },
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.None,
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                    )
                                    Spacer(
                                        Modifier
                                            .height(5.dp)
                                            .width(5.dp)
                                    )
                                }

                            }
                        }

                        //name
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "이름",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                        .width(100.dp)
                                        .padding(10.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                color = ContentLine,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        value = name,
                                        maxLines = 1,
                                        onValueChange = {},
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                    )
                                    Spacer(
                                        Modifier
                                            .height(5.dp)
                                            .width(5.dp)
                                    )
                                }


                            }
                        }

                        //전화번호
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "전화번호",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                        .width(100.dp)
                                        .padding(10.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .border(
                                                width = 1.dp,
                                                color = ContentLine,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        value = tel,
                                        maxLines = 1,
                                        onValueChange = {},
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                    )
                                    Spacer(
                                        Modifier
                                            .height(5.dp)
                                            .width(5.dp)
                                    )

                                }

                            }
                        }

                        //비밀번호
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Password",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                        .width(100.dp)
                                        .padding(10.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .focusRequester(focusRequester)
                                            .border(
                                                width = 1.dp,
                                                color = ContentLine,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        value = password,
                                        maxLines = 1,
                                        onValueChange = {
                                            password = it
                                        },
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        visualTransformation = PasswordVisualTransformation(),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.White,
                                        ),

                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                        keyboardActions = KeyboardActions(
                                            onNext = {
                                                focusRequester.requestFocus()
                                            },
                                        ),
                                    )
                                    Spacer(
                                        Modifier
                                            .height(5.dp)
                                            .width(5.dp)
                                    )

                                }
                            }
                        }

                        //비밀번호 확인
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Password 확인",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(4f)
                                        .width(100.dp)
                                        .padding(10.dp)
                                ) {
                                    TextField(
                                        modifier = Modifier
                                            .focusRequester(focusRequester)
                                            .border(
                                                width = 1.dp,
                                                color = ContentLine,
                                                shape = RoundedCornerShape(5.dp)
                                            ),
                                        value = checkPassword,
                                        maxLines = 1,
                                        onValueChange = {
                                            checkPassword = it
                                        },
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        visualTransformation = PasswordVisualTransformation(),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.White,
                                        ),

                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                keyboardController?.hide()
                                            },
                                        ),
                                    )
                                    Spacer(
                                        Modifier
                                            .height(5.dp)
                                            .width(5.dp)
                                    )

                                }
                            }
                        }

                        //sms 전송
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {

                                    Text(
                                        text = "SMS 전송",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }


                                Row(
                                    modifier = Modifier
                                        .weight(4f)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = smsCheckedState.value,
                                        onCheckedChange = { smsCheckedState.value = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color.LightGray,
                                            uncheckedColor = Color.White
                                        )
                                    )

                                    Spacer(Modifier.width(5.dp))
                                    Text("수신", color = Color.White)
                                }

                            }
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
                                        if (password == checkPassword) {
                                            viewModel.updateUserPassword(
                                                userId,
                                                computeSHAHash(password)
                                            )
                                        } else {
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                                            }
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

@Preview
@Composable
fun ManagerPasswordEditPreview() {
    val smsCheckedState = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .padding(10.dp)
            .selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = smsCheckedState.value,
            onCheckedChange = { smsCheckedState.value = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.LightGray,
                uncheckedColor = Color.White
            )
        )

        Spacer(Modifier.width(5.dp))
        Text("수신", color = Color.White)
    }
}