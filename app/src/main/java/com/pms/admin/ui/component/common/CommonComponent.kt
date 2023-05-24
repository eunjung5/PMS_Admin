package com.pms.admin.ui.component.common

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pms.admin.MainActivity
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.DisableEditBackground
import com.pms.admin.ui.theme.PopupTitleBackground
import com.pms.admin.ui.views.managerManagement.FocusItem
import com.pms.admin.util.NetworkManager
import kotlinx.coroutines.*

/**
 * RegisterItem
 * title: 등록 이름
 * item: 등록할 이름
 * assistance: textbox 아래 설명 내용
 * required: 필수 항목
 * focusItem: current focus item, next focus item
 * disabled: edit mode일 경우, 입력되지 않돍
 * weight: for width
 * inputType: text keyboard 창을 위한 type 지정
 * onDone: 완료 event handler
 * onTextValueChange: text change updated
 */
@Composable
fun RegisterItem(
    title: String,
    item: String,
    assistance: String = "",
    required: Boolean = false,
    focusItem: FocusItem,
    disabled: Boolean = false,
    maxLine: Int = 1,
    weight: Float = 1f,
    password:Boolean = false,
    inputType: KeyboardType = KeyboardType.Text,
    onDone: (() -> Unit)? = null,
    onTextValueChange: (String) -> Unit,
) {
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
                    .weight(weight)
                    .padding(top = 30.dp),
            ) {
                if (required) {
                    Text(text = "*", color = Color.Red)
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
                            width = 1.dp, color = ContentLine, shape = RoundedCornerShape(5.dp)
                        )
                        .focusRequester(focusItem.currentFocus)
                        .focusProperties {
                            focusItem.nextFocus?.let {
                                if (it != null) {
                                    next = it
                                }
                            }
                        },
                    value = item,
                    maxLines = maxLine,
                    onValueChange = onTextValueChange,
                    shape = RoundedCornerShape(5.dp),
                    enabled = !disabled,
                    textStyle = TextStyle(
                        color = if (disabled) Color.Gray else Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = ContentsBackground,
                        focusedIndicatorColor = Color.Transparent,   //hide the indicator
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                    ),
                    keyboardOptions = if (onDone != null)
                            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        else KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = inputType,
                        ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Next)
                    }, onDone = {
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
                    text = assistance,
                    color = Color.LightGray,
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


@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest, properties = properties
    ) {
        content()
    }
}

@Composable
fun DeleteDialogEditItem(
    title: String, content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.5f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = MaterialTheme.typography.body1.fontSize,
                modifier = Modifier.padding(5.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ) {
            Text(
                text = content,
                color = Color.White,
                fontSize = MaterialTheme.typography.body1.fontSize,
                modifier = Modifier
                    .width(300.dp)
                    .background(DisableEditBackground, shape = RoundedCornerShape(5.dp))
                    .padding(10.dp)
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CheckAuthorityAndFinish() {
    val context = LocalContext.current
    val activity = (context as? Activity)
    val scope = rememberCoroutineScope()
    var finishDialog by remember { mutableStateOf(false) } //서버에 권한 체크해서, 계정 문제 있을때, 종료 되도록 팝업

    scope.launch {
        if (!NetworkManager.checkAuthority()) {
            Log.e("eun", "check authority = ${activity} ")

            // activity?.finish()
            finishDialog = true
        }
    }

    if (finishDialog) {
        val activity = (LocalContext.current as? Activity)
        CustomAlertDialog(onDismissRequest = {
            finishDialog = false
            activity?.finish()
        }) {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .height(200.dp)
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
                    Text(stringResource(R.string.no_authority_message), color = Color.White)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, end = 30.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                finishDialog = false
                                activity?.finish()
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


//custom snackbar
suspend fun showSnackBar(
    scaffoldState: ScaffoldState,
    message: String = "show Message",
    actionLabel: String? = "OK",
    duration: SnackbarDuration = SnackbarDuration.Short
) {
Log.e(TAG,"showSnackBar")
    Dispatchers.Main{
        val job = launch{
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = duration,
            ).let {
               when (it) {
                   SnackbarResult.Dismissed -> Log.d(MainActivity.TAG, "스낵바 닫아짐")
                   SnackbarResult.ActionPerformed -> Log.d(MainActivity.TAG, "MYSnackBar: 스낵바 확인 버튼 클릭")
               }
           }
    }

       delay(1500)
       job.cancel()
    }



}
