package com.pms.admin.ui.views.jobHistory

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.*
import com.pms.admin.R
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.response.JobListResult
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.CalendarBackground
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.ManagerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InquiryByControl(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    managerViewModel: ManagerViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    ),
) {

    var jobList = managerViewModel.jobList
    val window = rememberWindowSize()
    // 출력 포맷 지정
    var dataFormat = SimpleDateFormat("yyyy/M/d")

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var before7Calendar by remember {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -7)
        mutableStateOf(calendar)
    }

    var currentDate by remember { mutableStateOf(dataFormat.format(calendar.time)) }
    var before7Date by remember {
        mutableStateOf(dataFormat.format(before7Calendar.time))
    }

    var searchContent by remember { mutableStateOf("") }

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayofMonth ->
            calendar.set(year, month, dayofMonth)
            currentDate = "$year/${month + 1}/$dayofMonth"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val before7DatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayofMonth ->
            before7Calendar.set(year, month, dayofMonth)
            before7Date = "$year/${month + 1}/$dayofMonth"
        },
        before7Calendar.get(Calendar.YEAR),
        before7Calendar.get(Calendar.MONTH),
        before7Calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(true) {
//        managerViewModel.getJobList(
//            user_id = userId,
//            start_date = before7Date,
//            end_date = currentDate,
//            contents = searchContent,
//            page = 0
//        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        SidebarMenu(navController, 0)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.inquiry_by_control),
                R.drawable.person_search
            )

            //search mode button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 30.dp, end = 30.dp),
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
                                        text = stringResource(id = R.string.start_day),
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
                                        text = stringResource(id = R.string.end_day),
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
//                                            managerViewModel.getJobList(
//                                                user_id = userId,
//                                                start_date = before7Date,
//                                                end_date = currentDate,
//                                                contents = searchContent,
//                                                page = 0
//                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(backgroundColor = MenuBackground),
                                        shape = RoundedCornerShape(50.dp),

                                        ) {
                                        Text(
                                            text = stringResource(R.string.search),
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
                                        text = stringResource(R.string.inquiry_by_mpu),
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
                ControlJobList(
                    dataList = jobList.value,
                )
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.ControlJobList(
    dataList: List<JobListResult>,
) {
    val context = LocalContext.current
    val headers = listOf(
        stringResource(R.string.manager),
        stringResource(R.string.job_time),
        stringResource(R.string.job_content),
        stringResource(R.string.job_description)
    )
    val weights = listOf(1F, 2F, 3F, 3F)
    val scrollState = rememberScrollState()

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
                .padding(16.dp),
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
                            weight = 9F
                        )
                    }
                }
            }

            items(dataList) {
                val (user_id, work_date, work_type, dscr, result) = it

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