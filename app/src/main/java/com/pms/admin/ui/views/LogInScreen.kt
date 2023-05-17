package com.pms.admin.ui.pages

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.util.NetworkManager
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LogInScreen(
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
) {
    //아이디와 비밀번호 입력
    var id by rememberSaveable { mutableStateOf("admin2") }
    var password by rememberSaveable { mutableStateOf("admin2") }

    val scaffoldState = rememberScaffoldState()
    //text focus
    val focusRequester = remember { FocusRequester() }
    //keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tryLogin by rememberSaveable{ mutableStateOf(false) }

    val window = rememberWindowSize()

    Log.e(TAG, "window size = $window, viewmodel = ${viewModel}")
    LaunchedEffect(tryLogin){
        viewModel.logInSession.collect { connected ->
            Log.d(TAG,"LogInScreen  logInSession=    try : $tryLogin   connected = $connected")
            if(tryLogin)
            {
                if (!connected) {
                    scaffoldState.snackbarHostState.showSnackbar(viewModel.error.value)
                }

                tryLogin = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) {
        if(window.height == WindowType.Medium)
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(AdminBackground),

                ) {
                //logo image
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Image(painter = painterResource(id = R.drawable.logo),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(350.dp)
                            .height(250.dp))
                }

                //log in textbox
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    //id
                    OutlinedTextField(
                        modifier = Modifier
                            .border(
                                width = 3.dp,
                                color = Color.Unspecified,
                                shape = RoundedCornerShape(30.dp),
                            ),
//                        .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                        value = id,
                        placeholder = { Text(stringResource(id = R.string.id), color = Color.Gray) },
                        maxLines = 1,
                        onValueChange = { id = it },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {                //아이디 입력후 엔터키
                            focusRequester.requestFocus() //비밀번호 입력창에 포커스 주기
                        }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Red,
                            unfocusedBorderColor = Red),
                        shape = RoundedCornerShape(30.dp),
                        textStyle = TextStyle(color= Color.White, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //password
                    OutlinedTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .border(
                                width = 3.dp,
                                color = Color.Unspecified,
                                shape = RoundedCornerShape(30.dp),
                            ),
//                        .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                        value = password,
                        placeholder = { Text(stringResource(id = R.string.password), color = Color.Gray) },
                        maxLines = 1,
                        onValueChange = { password = it },
                        visualTransformation = PasswordVisualTransformation(),                               //비밀번호 *로 표시되도록 처리
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {                //아이디와 비밀번호 입력후 완료 입력시, 처리
                            keyboardController?.hide()

                            if (id.isNotBlank() && password.isNotBlank()) {

                                if(!NetworkManager.checkNetworkState(context)){
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("네트워크 상태 확인 바랍니다. ")
                                    }
                                }
                                else{
                                    tryLogin = true
                                    viewModel.login(id, computeSHAHash(password))
                                }

                            } else {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("아이디나 비밀번호를 입력하세요.")
                                }
                            }
                        }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Red,
                            unfocusedBorderColor = Red),
                        shape = RoundedCornerShape(30.dp),
                        textStyle = TextStyle(color= Color.White, fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.width(150.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(30.dp),
                        onClick = {

                            keyboardController?.hide()

                            if (id.isNotBlank() && password.isNotBlank()) {
                                if(!NetworkManager.checkNetworkState(context)){
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("네트워크 상태 확인 바랍니다. ")
                                    }
                                }
                                else
                                {
                                    tryLogin = true
                                    viewModel.login(id, computeSHAHash(password))
                                }

                            } else {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("아이디나 비밀번호를 입력하세요.")
                                }
                            }
                        }) {
                        Text(stringResource(id = R.string.login),
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                //address and contact
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    //address
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        if(this.minWidth > 500.dp){
                            Text(stringResource(id = R.string.company_address_fullwidth), color = Color.White )
                        }
                        else
                        {
                            Text(stringResource(id = R.string.company_address_normalwidth), color = Color.White )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //contact and mail url
                    Row(
                        modifier = Modifier.
                        fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        Text(stringResource(id = R.string.company_contact), color = Color.White )
                        Spacer(modifier = Modifier.width(10.dp))

                        val emailText = buildAnnotatedString {
                            pushStringAnnotation(tag = "URL",
                                annotation = "https://pms@urimts.co.kr")

                            withStyle(style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                            ) {
                                append(stringResource(id = R.string.company_email))
                            }
                            pop()
                        }

                        ClickableText(
                            text = emailText,
                            onClick = { offset ->
                                emailText.getStringAnnotations(tag = "URL", start = offset,
                                    end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        // If yes, we log its value
                                        Log.d("Clicked URL", annotation.item)
                                    }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //company address url
                    Row(
                        modifier = Modifier.
                        fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        val homeText = buildAnnotatedString {
                            pushStringAnnotation(tag = "URL",
                                annotation = "https://pms@urimts.co.kr")

                            withStyle(style = SpanStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                            ) {
                                append(stringResource(id = R.string.company_url))
                            }
                            pop()
                        }

                        ClickableText(
                            text = homeText,
                            onClick = { offset ->
                                homeText.getStringAnnotations(tag = "URL", start = offset,
                                    end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        // If yes, we log its value
                                        Log.d("Clicked URL", annotation.item)
                                    }
                            }
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
        }
        else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(ContentsBackground),
                ) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(start = 30.dp),
                    horizontalArrangement= Arrangement.Center,
                    verticalAlignment =  Alignment.CenterVertically
                )
                {
                    Image(painter = painterResource(id = R.drawable.logo),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(300.dp)
                            .height(250.dp))
                }

                //login text & company address
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxSize()
                        .padding(start= 50.dp,top=50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    //log in textbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement= Arrangement.Center,
                        verticalAlignment =  Alignment.CenterVertically
                    )
                    {
                        Column{
                            //id
                            OutlinedTextField(
                                modifier = Modifier
                                    .width(250.dp)
                                    .border(
                                        width = 3.dp,
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                ,
//                        .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                                value = id,
                                placeholder = { Text(stringResource(id = R.string.id), color = Color.Gray) },
                                maxLines = 1,
                                onValueChange = { id = it },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {                //아이디 입력후 엔터키
                                    focusRequester.requestFocus() //비밀번호 입력창에 포커스 주기
                                }),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Red,
                                    unfocusedBorderColor = Red),
                                shape = RoundedCornerShape(30.dp),
                                textStyle = TextStyle(color= Color.White, fontWeight = FontWeight.Bold)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            //password
                            OutlinedTextField(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .border(
                                        width = 3.dp,
                                        color = Color.Unspecified,
                                        shape = RoundedCornerShape(30.dp),
                                    )
                                    .width(250.dp),
//                        .onFocusChanged { focusState -> hasFocus = focusState.hasFocus },
                                value = password,
                                placeholder = { Text(stringResource(id = R.string.password), color = Color.Gray) },
                                maxLines = 1,
                                onValueChange = { password = it },
                                visualTransformation = PasswordVisualTransformation(),                               //비밀번호 *로 표시되도록 처리
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {                //아이디와 비밀번호 입력후 완료 입력시, 처리
                                    keyboardController?.hide()

                                    if (id.isNotBlank() && password.isNotBlank()) {

                                        if(!NetworkManager.checkNetworkState(context)){
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("네트워크 상태 확인 바랍니다. ")
                                            }
                                        }
                                        else{
                                            tryLogin = true
                                            viewModel.login(id, computeSHAHash(password))
                                        }

                                    } else {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar("아이디나 비밀번호를 입력하세요.")
                                        }
                                    }
                                }),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Red,
                                    unfocusedBorderColor = Red),
                                shape = RoundedCornerShape(30.dp),
                                textStyle = TextStyle(color= Color.White, fontWeight = FontWeight.Bold)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Button(
                                modifier = Modifier.width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Red,
                                    contentColor = Color.White,
                                ),
                                shape = RoundedCornerShape(30.dp),
                                onClick = {

                                    keyboardController?.hide()

                                    if (id.isNotBlank() && password.isNotBlank()) {
                                        if(!NetworkManager.checkNetworkState(context)){
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar("네트워크 상태 확인 바랍니다. ")
                                            }
                                        }
                                        else
                                        {
                                            tryLogin = true
                                            viewModel.login(id, computeSHAHash(password))
                                        }

                                    } else {
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar("아이디나 비밀번호를 입력하세요.")
                                        }
                                    }
                                }) {
                                Text(stringResource(id = R.string.login),
                                    fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    //address and contact
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        //address
                        Text(stringResource(id = R.string.company_address_normalwidth), color = Color.White )
                        Spacer(modifier = Modifier.height(5.dp))

                        //contact and mail url
                        Row(
                            modifier = Modifier.
                            fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        )
                        {
                            Text(stringResource(id = R.string.company_contact), color = Color.White )
                            Spacer(modifier = Modifier.width(10.dp))

                            val emailText = buildAnnotatedString {
                                pushStringAnnotation(tag = "URL",
                                    annotation = "https://pms@urimts.co.kr")

                                withStyle(style = SpanStyle(
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                ) {
                                    append(stringResource(id = R.string.company_email))
                                }
                                pop()
                            }

                            ClickableText(
                                text = emailText,
                                onClick = { offset ->
                                    emailText.getStringAnnotations(tag = "URL", start = offset,
                                        end = offset)
                                        .firstOrNull()?.let { annotation ->
                                            // If yes, we log its value
                                            Log.d("Clicked URL", annotation.item)
                                        }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        //company address url
                        Row(
                            modifier = Modifier.
                            fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ){
                            val homeText = buildAnnotatedString {
                                pushStringAnnotation(tag = "URL",
                                    annotation = "https://pms@urimts.co.kr")

                                withStyle(style = SpanStyle(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline,
                                )
                                ) {
                                    append(stringResource(id = R.string.company_url))
                                }
                                pop()
                            }

                            ClickableText(
                                text = homeText,
                                onClick = { offset ->
                                    homeText.getStringAnnotations(tag = "URL", start = offset,
                                        end = offset)
                                        .firstOrNull()?.let { annotation ->
                                            // If yes, we log its value
                                            Log.d("Clicked URL", annotation.item)
                                        }
                                }
                            )
                        }
                    }
                }




            }
        }


    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
fun PreviewLoginScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            //.padding(it)
            .background(ContentsBackground),
        horizontalArrangement= Arrangement.Center,
        verticalAlignment= Alignment.CenterVertically,
        ) {


            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(350.dp)
                        .height(250.dp))
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("id", color = Color.White)
                Text("password", color = Color.White)
            }

    }
}