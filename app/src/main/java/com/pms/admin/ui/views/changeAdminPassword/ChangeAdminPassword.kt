package com.pms.admin.ui.views.changeAdminPassword

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.common.CustomAlertDialog
import com.pms.admin.ui.component.common.RegisterItem
import com.pms.admin.ui.component.common.showSnackBar
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.PopupTitleBackground
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.views.managerManagement.FocusItem
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChangeAdminPassword(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    ) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = rememberWindowSize()
    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기

    val (currentFocus, newFocus, confirmFocus) = FocusRequester.createRefs()     //next text focus
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()

    val pref: SharedPreferences =
        context.getSharedPreferences("com.pms.admin.session_manager.SESSION_PREFERENCES", 0);
    val adminID = pref.getString("com.pms.admin.session_manager.SESSION_USER_ID", "") ?: " "
    var finishDialog by remember { mutableStateOf(false) }


    LaunchedEffect(true){
        viewModel.changePassword.collect{result ->
            val message = if(result) String.format(context.resources.getString(R.string.change_password_message)," ") else  String.format(context.resources.getString(R.string.change_password_message),"이 실패 ")
            if(result) {
                    showSnackBar(scaffoldState = scaffoldState, message = message)
                finishDialog = true
            }else{
                showSnackBar(scaffoldState = scaffoldState, message = message)
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
            SidebarMenu(navController, 6)

            Column(modifier = Modifier.fillMaxSize()) {

                    Header(
                        navController,
                        viewModel,
                        stringResource(id = R.string.change_admin_password),
                        R.drawable.menu_lock
                    )

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

                        //현재 admin pw
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.current_admin_password),
                                item = currentPassword,
                                password=true,
                                weight = 2f,
                                focusItem = FocusItem(currentFocus, newFocus),
                                onTextValueChange = {currentPassword = it }
                            )
                        }

                        //새 admin pw
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.new_admin_password),
                                item = newPassword,
                                password=true,
                                weight = 2f,
                                focusItem = FocusItem(newFocus, confirmFocus),
                                onTextValueChange = {newPassword = it }
                            )
                        }

                        //새 admin pw 확인
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.confirm_new_admin_password),
                                item = confirmPassword,
                                password=true,
                                weight = 2f,
                                focusItem = FocusItem(confirmFocus,null ),
                                onTextValueChange = {confirmPassword = it },
                                onDone={
                                    keyboardController?.hide()

                                    if(newPassword != confirmPassword){
                                        scope.launch{
                                            showSnackBar(scaffoldState = scaffoldState, message = context.resources.getString(R.string.not_matched_new_password))
                                        }
                                    }else{
                                        viewModel.setPasswordChange(userId = adminID, sha1 = computeSHAHash(currentPassword), newSha1 =computeSHAHash(newPassword) )
                                    }
                                }
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
                                        if(newPassword != confirmPassword){
                                            scope.launch{
                                                showSnackBar(scaffoldState = scaffoldState, message = context.resources.getString(R.string.not_matched_new_password))
                                            }
                                        }else{
                                            viewModel.setPasswordChange(userId = adminID, sha1 = computeSHAHash(currentPassword), newSha1 =computeSHAHash(newPassword) )
                                        }

                                    }) {
                                    Text(
                                        text = stringResource(id = R.string.store),
                                        color = Color.White
                                    )
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
                                        text = stringResource(id = R.string.cancel),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                }

            }

        }

        if (finishDialog) {
            CustomAlertDialog(onDismissRequest = {
                finishDialog = false
            }) {
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .height(250.dp)
                        .padding(30.dp)
                        .background(ContentsBackground)

                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PopupTitleBackground)
                            .padding(10.dp), horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.manager_notify),
                            color = Color.White,
                            fontSize = MaterialTheme.typography.h6.fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(modifier = Modifier.padding(start = 30.dp, top = 30.dp)) {
                        Text(stringResource(R.string.change_password_restart_app), color = Color.White)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, end = 30.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    finishDialog = false
                                    val packageManager = context.packageManager
                                    val intent =
                                        packageManager.getLaunchIntentForPackage(context.packageName)
                                    val componentName: ComponentName? = intent!!.component
                                    val mainIntent = Intent.makeRestartActivityTask(componentName)
                                    context.startActivity(mainIntent)
                                    Runtime.getRuntime().exit(0)

                                }, colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Red, contentColor = Color.White
                                )
                            ) {
                                Text(stringResource(id = R.string.confirm))
                            }
                        }
                    }
                }

            }
        }
    }
}