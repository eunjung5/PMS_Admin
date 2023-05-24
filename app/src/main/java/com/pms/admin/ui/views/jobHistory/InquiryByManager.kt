package com.pms.admin.ui.views.jobHistory

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pms.admin.PMSAdminApplication
import com.pms.admin.R
import com.pms.admin.WindowType
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.model.response.JobListResult
import com.pms.admin.model.response.UserIdListResult
import com.pms.admin.model.response.UserJobListResult
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.component.table.TableCell
import com.pms.admin.ui.theme.CalendarBackground
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.JobHistoryViewModel
import com.pms.admin.ui.viewModels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InquiryByManager(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity),
    jobHistoryViewModel: JobHistoryViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    ),
) {

    val window = rememberWindowSize()
    val context = LocalContext.current
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


    var jobList by remember { mutableStateOf(emptyList<UserJobListResult>()) }
    var userId by remember {mutableStateOf("")}
    var userIdList by remember { mutableStateOf(emptyList<UserIdListResult>()) }

    LaunchedEffect(true) {
        jobHistoryViewModel.getUserIdList()

        jobHistoryViewModel.userIdList.collect {
            userIdList = it
            userId = userIdList[0].user_id
        }
    }

    LaunchedEffect(userId) {
        jobHistoryViewModel.getDateIdSearch(
            startDate = before7Date,
            endDate = currentDate,
            userId = userId,
            page = 0
        )

        jobHistoryViewModel.jobDataIdList.collect {
            jobList = it
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        SidebarMenu(navController, 3)

        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                navController,
                viewModel,
                stringResource(id = R.string.inquiry_by_user),
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
                                            jobHistoryViewModel.getDateIdSearch(
                                                startDate = before7Date,
                                                endDate = currentDate,
                                                userId = userId,
                                                page = 0
                                            )
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

                            //이동 메뉴 버튼

                            //작업자 선택
                            Row(
                                modifier = Modifier.weight(3f),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = stringResource(R.string.job_user_inquiry),
                                        color = Color.White,
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))

                                    //user id list
                                    UserIdList(userIdList) {
                                        userId = it
                                    }



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
                DateIdJobList(
                    dataList = jobList,
                )
            }
        }

    }
}

@Composable
fun UserIdList(
    userIdList:List<UserIdListResult>,
    selectUserId: (userId:String)->Unit,
) {
    var isExpandedList by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }

    LaunchedEffect(userIdList){
        userId = if(userIdList.isNotEmpty())userIdList[0].user_id else ""
        selectUserId(userId)
    }
    Button(
        onClick = { isExpandedList = !isExpandedList },
        modifier = Modifier
            .width(200.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = CalendarBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CalendarBackground),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = userId, color = Color.White)

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "down"
            )
        }
    }

    DropdownMenu(
        modifier = Modifier
            .width(200.dp)
            .background(CalendarBackground)
            .requiredSizeIn(maxHeight = 200.dp),
        expanded = isExpandedList,
        onDismissRequest = { isExpandedList = false },
    ) {

        userIdList?.forEach { user ->
            DropdownMenuItem(
                modifier = Modifier.width(200.dp),
                onClick = {
                    isExpandedList = false
                    userId = user.user_id
                    selectUserId(userId)

                }) {
                Text(text = user.user_id, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.DateIdJobList(
    dataList: List<UserJobListResult>,
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
                            weight = 9F
                        )
                    }
                }
            }

            items(dataList) {
                val (user_id, work_date, work_type) = it

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TableCell(text = user_id, weight = weights[0])
                    TableCell(text = work_date, weight = weights[1])
                    TableCell(text = work_type, weight = weights[2])
                    TableCell(text = "", weight = weights[3])
                }
            }
        }
    }

}