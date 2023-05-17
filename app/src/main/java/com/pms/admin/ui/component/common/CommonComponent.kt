package com.pms.admin.ui.component.common

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.pms.admin.MainActivity
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.views.managerManagement.FocusItem

@Composable
fun RegisterItem(
    title: String,
    item: String,
    assistance: String ="",
    required: Boolean,
    focusItem: FocusItem,
    disabled: Boolean = false,
    maxLine: Int = 1,
    onDone: (() -> Unit)? = null,
    onTextValueChange: (String) -> Unit,
) {
    var content by remember { mutableStateOf(item) }
    Log.d(MainActivity.TAG, "$title = $item, content = $content")
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
                            width = 1.dp,
                            color = ContentLine,
                            shape = RoundedCornerShape(5.dp)
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
                        color = if(disabled) Color.Gray else Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    visualTransformation = if (title === "Password") PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = ContentsBackground,
                        focusedIndicatorColor = Color.Transparent,   //hide the indicator
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                    ),

                    keyboardOptions = if (onDone != null) KeyboardOptions.Default.copy(imeAction = ImeAction.Done) else KeyboardOptions.Default.copy(
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

