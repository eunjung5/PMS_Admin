package com.pms.admin.ui.views.managerManagement

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.model.JobListResult
import com.pms.admin.model.ManagerListResult
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.MainViewModel
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.CalendarBackground
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ManagerJobSearch(
    navController: NavHostController,
    viewModel: MainViewModel,
    userId: String,
) {
    var jobList = viewModel.jobList
    val window = rememberWindowSize()
    // 출력 포맷 지정
    var dataFormat = SimpleDateFormat("yyyy/M/d")

    var calendar by rememberSaveable{mutableStateOf(Calendar.getInstance())}
    var before7Calendar by rememberSaveable{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -7)
        mutableStateOf(calendar)
    }

    var currentDate by rememberSaveable { mutableStateOf(dataFormat.format(calendar.time)) }
    var before7Date by rememberSaveable {
        mutableStateOf(dataFormat.format(before7Calendar.time))
    }

    var searchContent by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context ,
        { _, year, month, dayofMonth ->
            calendar.set(year,month,dayofMonth)
            currentDate =  "$year/${month+1}/$dayofMonth"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val before7DatePickerDialog = DatePickerDialog(
        context ,
        { _, year, month, dayofMonth ->
            before7Calendar.set(year,month,dayofMonth)
            before7Date =  "$year/${month+1}/$dayofMonth"
        },
        before7Calendar.get(Calendar.YEAR),
        before7Calendar.get(Calendar.MONTH),
        before7Calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(key1 = true) {
        viewModel.getJobList(
            user_id = userId,
            start_date = before7Date,
            end_date = currentDate,
            contents = searchContent,
            page = 0)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        SidebarMenu(navController, 0)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(navController, viewModel, "관리자 작업조회", R.drawable.person_search)

            //search mode button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp),
                )
                {
                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp),
                        ) {
                            //시작 날짜
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "시작 날짜",
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    TextField(
                                        modifier = Modifier
                                            .padding(start = 5.dp, end = 5.dp)
                                            .clickable { before7DatePickerDialog.show() },
                                        readOnly = true,
                                        value = before7Date,
                                        maxLines = 1,
                                        onValueChange = {},
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.None,
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = CalendarBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.calendar),
                                                contentDescription = "calendar icon",
                                                modifier = Modifier
                                                    .width(20.dp)
                                                    .clickable { before7DatePickerDialog.show() },
                                                tint = Color.White,
                                            )
                                        }
                                    )

                                }
                            }

                            Spacer(Modifier.width(10.dp))

                            //종료 날짜
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "종료 날짜",
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    TextField(
                                        modifier = Modifier
                                            .padding(start = 5.dp, end = 5.dp)
                                            .clickable { datePickerDialog.show() },
                                        readOnly = true,
                                        value = currentDate,
                                        maxLines = 1,
                                        onValueChange = {},
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.None,
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = CalendarBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                        ),
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.calendar),
                                                contentDescription = "calendar icon",
                                                modifier = Modifier
                                                    .width(20.dp)
                                                    .clickable { datePickerDialog.show() },
                                                tint = Color.White,

                                            )
                                        }
                                    )
                                }
                            }

                            //조회 
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column(
                                    modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.getJobList(
                                                user_id = userId,
                                                start_date = before7Date,
                                                end_date = currentDate,
                                                contents = searchContent,
                                                page = 0)
                                        },
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MenuBackground),
                                        shape = RoundedCornerShape(50.dp),

                                        ) {
                                        Text(
                                            text = "조회",
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            //작업 내용 조회
                            Row(
                                modifier = Modifier.weight(3f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = "작업 내용 조회",
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    TextField(
                                        modifier = Modifier
                                            .padding(start = 5.dp, end = 5.dp),
                                        value = searchContent,
                                        maxLines = 1,
                                        onValueChange = { searchContent = it },
                                        shape = RoundedCornerShape(5.dp),
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.None,
                                        ),
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = ContentsBackground,
                                            focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.White,
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = {

                                        }),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Outlined.Search,
                                                contentDescription = "search icon",
                                                tint = Color.White
                                            )
                                        }
                                    )

                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(if (window.height == WindowType.Medium) 4f else 3f)
            ) {
                ManagerJobList(
                    dataList = jobList.value,
                )
            }
        }

    }
}
@Composable
fun ColumnScope.ManagerJobList(
    dataList: List<JobListResult>,
) {

    val headers = listOf("관리자", "작업시간", "작업내용", "상세설명")
    val weights = listOf(1F, 2F, 3F,  1.5F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(3f)
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Row(Modifier.background(MenuBackground)) {
                    for (i in 0 until headers.size) {
                        TableCell(text = headers[i], weight = weights[i])
                    }

                }
            }

            if (dataList.isEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth()) {
                        TableCell(text = "내용 없음", weight = 1F)
                    }
                }
            }

            items(dataList) {
                val (user_id,work_date,work_type,dscr,result) = it


                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = user_id, weight = weights[0])
                    TableCell(text = work_date, weight = weights[1])
                    TableCell(text = work_type, weight = weights[2])
                    TableCell(text = dscr, weight = weights[3])
                 }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    showBackground = true,
    backgroundColor = 0x000000,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun ManagerJobSearchPreview() {

}