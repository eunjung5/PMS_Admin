package com.pms.admin.ui.views.mpuManagement

import android.util.Log
import android.widget.ExpandableListView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.pms.admin.model.data.Mode
import com.pms.admin.rememberWindowSize
import com.pms.admin.ui.component.common.RegisterItem
import com.pms.admin.ui.component.common.showSnackBar
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.theme.MenuBackground
import com.pms.admin.ui.viewModels.MPUViewModel
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.views.managerManagement.FocusItem
import com.pms.admin.util.NetworkManager
import com.pms.admin.util.computeSHAHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MPUAddEdit(
    navController: NavHostController,
    mpuId: String,
    mpuViewModel: MPUViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    ),
    mode: Mode,
) {
    val viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var id by remember { mutableStateOf("") }
    var siteId by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var managerContact by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var cbuCount by remember { mutableStateOf("") }
    var bmsCount by remember { mutableStateOf("") }

    var showSiteIdList by remember { mutableStateOf(false) }  //siteId 클릭시, 리스트 보여주기
    var siteIdList by remember { mutableStateOf<List<Int>>(emptyList()) }

    var checkDuplicationID by remember { mutableStateOf(mpuId.isNotEmpty()) }   //mpu id 중복체크 여부 확인

    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기

    val focusManager = LocalFocusManager.current
    val (idFocus, nameFocus, contactFocus, capacityFocus, cbuFocus, bmsFocus) = FocusRequester.createRefs()     //next text focus

    val window = rememberWindowSize()


    LaunchedEffect(true) {
        if (mode == Mode.Add) {
            mpuViewModel.getSiteIDList()

            mpuViewModel.siteList.collect {
                siteId = it.site_id[0].toString()
                siteIdList = it.site_id
            }
        }
        else {
            mpuViewModel.getMPUInfo(mpuId)

            mpuViewModel.mpuInfo.collect { mpu->
                Log.e(TAG, "${mpu}")
                id = mpu.mpu_id
                siteId = mpu.site_id
                managerName = mpu.op_name
                managerContact = mpu.op_tel
                capacity = mpu.capacity
                cbuCount = mpu.cbu1.toString()
                bmsCount = mpu.bms1.toString()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), scaffoldState = scaffoldState
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBackground)
                .padding(it)
        ) {
            SidebarMenu(navController, 2)

            Column(modifier = Modifier.fillMaxSize()) {
                if (mode == Mode.Add) Header(
                    navController,
                    viewModel,
                    stringResource(id = com.pms.admin.R.string.mpu_create),
                    com.pms.admin.R.drawable.add_circle
                )//아이콘 차후 수정
                else Header(
                    navController,
                    viewModel,
                    stringResource(id = com.pms.admin.R.string.mpu_modify),
                    com.pms.admin.R.drawable.edit
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

                        //mpu id text
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
                                            .weight(1.5f)
                                            .padding(top = 30.dp)
                                    ) {
                                        if (mode == Mode.Add) {
                                            Text(text = "*", color = Color.Red)
                                        }

                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = stringResource(id = com.pms.admin.R.string.mpu_id),
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
                                                value = if (mode == Mode.Add) id else mpuId,
                                                readOnly = mode == Mode.Edit,
                                                maxLines = 1,
                                                onValueChange = {
                                                    id = it
                                                },
                                                shape = RoundedCornerShape(5.dp),
                                                textStyle = TextStyle(
                                                    color = if(mode == Mode.Add) Color.White else Color.Gray,
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
                                                    imeAction = ImeAction.Next,
                                                    keyboardType = KeyboardType.Number
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
                                            ), verticalAlignment = Alignment.Top
                                    ) {
                                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                            onClick = {
                                                keyboardController?.hide()

                                                scope.launch {
                                                    if (id.isEmpty()) {
                                                        showSnackBar(
                                                            scaffoldState = scaffoldState,
                                                            message = context.resources.getString(
                                                                com.pms.admin.R.string.no_mpu_id
                                                            )
                                                        )
                                                        return@launch
                                                    }

                                                    mpuViewModel.checkDuplicatedMPUID(id)

                                                    mpuViewModel.checkDuplicatedMPUId.collect { result ->
                                                        val message =
                                                            if (result) context.resources.getString(
                                                                com.pms.admin.R.string.duplicate_mpu
                                                            ) else context.resources.getString(com.pms.admin.R.string.available_mpu)

                                                        checkDuplicationID = !result
                                                        showSnackBar(
                                                            scaffoldState = scaffoldState,
                                                            message = message
                                                        )
                                                    }
                                                }
                                            }) {
                                            Text(
                                                text = stringResource(id = com.pms.admin.R.string.duplicate_check),
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

                        //site id text
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
                                            .weight(1.5f)
                                            .padding(top = 30.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = stringResource(id = com.pms.admin.R.string.site_id),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(5f)
//                                            .padding(10.dp, bottom = 0.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp)
                                            ) {
                                            Button(modifier = Modifier.background(ContentsBackground),
                                                border = BorderStroke(
                                                    width = 1.dp, color = ContentLine
                                                ),
                                                shape = RoundedCornerShape(5.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = ContentsBackground,
                                                    contentColor = Color.White,
                                                    disabledBackgroundColor = ContentsBackground,
                                                    disabledContentColor= Color.Gray,
                                                ),
                                                enabled = (mode ==Mode.Add),
                                                onClick = { showSiteIdList = !showSiteIdList }) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(text = siteId)
                                                    if(mode == Mode.Add) {
                                                        Icon(
                                                            imageVector = Icons.Default.ArrowDropDown,
                                                            contentDescription = "down"
                                                        )
                                                    }
                                                }
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

                        //site id button 클릭시, list 나오도록
                        if (showSiteIdList) {
                            items(siteIdList.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 30.dp, top = 0.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(4f)
                                            .padding(start = 15.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1.5f)
                                                .padding(top = 30.dp),
                                        ) {}

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(5f)
                                                .width(100.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = ContentLine,
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = siteIdList[index].toString(),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(5.dp)
                                                    .clickable {
                                                        siteId = siteIdList[index].toString()
                                                        showSiteIdList = false
                                                    },
                                                color = Color.White
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
                        }

                        //manager name text
                        item {
                            RegisterItem(title = stringResource(id = com.pms.admin.R.string.safe_manager_name),
                                item = managerName,
                                focusItem = FocusItem(nameFocus, contactFocus),
                                weight = 1.5f,
                                onTextValueChange = { managerName = it })
                        }

                        //manager contact text
                        item {
                            RegisterItem(title = stringResource(id = com.pms.admin.R.string.safe_manager_contact),
                                item = managerContact,
                                focusItem = FocusItem(contactFocus, capacityFocus),
                                weight = 1.5f,
                                inputType = KeyboardType.Phone,
                                onTextValueChange = { managerContact = it })
                        }

                        //발전 용량
                        item {
                            RegisterItem(title = stringResource(id = com.pms.admin.R.string.mpu_capacity),
                                item = capacity,
                                focusItem = FocusItem(capacityFocus, cbuFocus),
                                weight = 1.5f,
                                inputType = KeyboardType.Number,
                                onTextValueChange = { capacity = it })
                        }

                        //mpu 구성
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
                                            text = stringResource(id = com.pms.admin.R.string.mpu_composition),
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

                                            //index 0~6까지
                                            LazyRow(modifier = Modifier.fillMaxWidth()) {
                                                mpuViewModel.mpuConfiguration.forEachIndexed { index, config ->
                                                    if (index < 7) {
                                                        item {
                                                            Row(
                                                                modifier = Modifier.width(80.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                Text(
                                                                    config.title,
                                                                    color = Color.White
                                                                )
                                                                Checkbox(
                                                                    checked = config.checked,
                                                                    onCheckedChange = { checked ->
                                                                        mpuViewModel.setMPUConfigurationChecked(
                                                                            index,
                                                                            checked
                                                                        )
                                                                    },
                                                                    colors = CheckboxDefaults.colors(
                                                                        checkedColor = Color.LightGray,
                                                                        uncheckedColor = Color.White
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            //index 7~13까지
                                            LazyRow(modifier = Modifier.fillMaxWidth()) {
                                                mpuViewModel.mpuConfiguration.forEachIndexed { index, config ->
                                                    if (index >= 7) {
                                                        item {
                                                            Row(
                                                                modifier = Modifier.width(80.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                Text(
                                                                    config.title,
                                                                    color = if (config.enable) Color.White else Color.Gray
                                                                )
                                                                Checkbox(
                                                                    checked = config.checked,
                                                                    enabled = config.enable,
                                                                    onCheckedChange = { checked ->
                                                                        mpuViewModel.setMPUConfigurationChecked(
                                                                            index,
                                                                            checked
                                                                        )
                                                                    },
                                                                    colors = CheckboxDefaults.colors(
                                                                        checkedColor = Color.LightGray,
                                                                        uncheckedColor = Color.White,
                                                                        disabledColor = Color.Gray
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }

                                                }

                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextField(
                                                    value = cbuCount,
                                                    onValueChange = { count ->

                                                        if (count.toInt() < 16) {
                                                            cbuCount = count
                                                        } else {
                                                            keyboardController?.hide()
                                                            scope.launch {
                                                                showSnackBar(
                                                                    scaffoldState = scaffoldState,
                                                                    message = context.resources.getString(
                                                                        R.string.not_max_16
                                                                    ),
                                                                )
                                                            }

                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .width(150.dp)
                                                        .border(
                                                            width = 1.dp,
                                                            color = ContentLine,
                                                            shape = RoundedCornerShape(5.dp)
                                                        )
                                                        .focusRequester(cbuFocus)
                                                        .focusProperties {
                                                            next = bmsFocus
                                                        },
                                                    maxLines = 1,
                                                    placeholder = {
                                                        Text(
                                                            "CBU 개수",
                                                            color = Color.LightGray
                                                        )
                                                    },
                                                    textStyle = TextStyle(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    colors = TextFieldDefaults.textFieldColors(
                                                        backgroundColor = ContentsBackground,
                                                        focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        cursorColor = Color.White,
                                                    ),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Next,
                                                        keyboardType = KeyboardType.Number
                                                    ),

                                                    )
                                                Spacer(Modifier.width(5.dp))

                                                TextField(
                                                    value = bmsCount,
                                                    onValueChange = { count ->
                                                        if (count.toInt() < 16) {
                                                            bmsCount = count
                                                        } else {
                                                            scope.launch {
                                                                keyboardController?.hide()

                                                                showSnackBar(
                                                                    scaffoldState = scaffoldState,
                                                                    message = context.resources.getString(
                                                                        R.string.not_max_16
                                                                    ),
                                                                )
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .width(150.dp)
                                                        .border(
                                                            width = 1.dp,
                                                            color = ContentLine,
                                                            shape = RoundedCornerShape(5.dp)
                                                        )
                                                        .focusRequester(bmsFocus)
                                                        .focusProperties {

                                                        },
                                                    maxLines = 1,
                                                    placeholder = {
                                                        Text(
                                                            "BMS 개수",
                                                            color = Color.LightGray
                                                        )
                                                    },
                                                    textStyle = TextStyle(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    colors = TextFieldDefaults.textFieldColors(
                                                        backgroundColor = ContentsBackground,
                                                        focusedIndicatorColor = Color.Transparent,   //hide the indicator
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        cursorColor = Color.White,
                                                    ),
                                                    keyboardOptions = KeyboardOptions.Default.copy(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        keyboardController?.hide()

                                                        var message: String = ""
                                                        if (checkDuplicationID) {
                                                            if (id.isNotEmpty() && capacity.isNotEmpty()) {
                                                                mpuViewModel.registerMPUInfo(
                                                                    mode,
                                                                    if (mode == Mode.Add) id else mpuId,
                                                                    siteId,
                                                                    managerName,
                                                                    managerContact,
                                                                    capacity,
                                                                    if (cbuCount.isEmpty()) 0 else cbuCount.toInt(),
                                                                    if (bmsCount.isEmpty()) 0 else bmsCount.toInt(),
                                                                )

                                                            } else {
                                                                message =
                                                                    context.resources.getString(R.string.required_msg)
                                                            }
                                                        } else {
                                                            message = String.format(
                                                                context.resources.getString(
                                                                    R.string.duplicate_confirm,
                                                                    "MPU ID"
                                                                )
                                                            )

                                                        }

                                                        if (message.isNotEmpty()) {
                                                            scope.launch {
                                                                showSnackBar(
                                                                    scaffoldState = scaffoldState,
                                                                    message = message
                                                                )
                                                            }
                                                        }

                                                        scope.launch {
                                                            mpuViewModel.result.collect { response ->
                                                                val result =
                                                                    if (mode == Mode.Add) context.resources.getString(
                                                                        R.string.register
                                                                    ) else context.resources.getString(
                                                                        R.string.modify
                                                                    )

                                                                if (response) {
                                                                    showSnackBar(
                                                                        scaffoldState = scaffoldState,
                                                                        message = String.format(
                                                                            context.resources.getString(
                                                                                R.string.mpu_success
                                                                            ), result
                                                                        ),
                                                                    )

                                                                    navController.popBackStack()
                                                                } else {
                                                                    showSnackBar(
                                                                        scaffoldState = scaffoldState,
                                                                        message = String.format(
                                                                            context.resources.getString(
                                                                                R.string.mpu_fail
                                                                            ),
                                                                            result
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }


                                                    }),
                                                )
                                            }
                                        }
                                    }

                                }
                            }
                        }


                        //저장 and 취소 buttons
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 30.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(modifier = Modifier.border(
                                    width = 1.dp,
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                                    onClick = {
                                        var message: String = ""
                                        if (checkDuplicationID) {
                                            if (id.isNotEmpty() && capacity.isNotEmpty()) {
                                                mpuViewModel.registerMPUInfo(
                                                    mode,
                                                    if (mode == Mode.Add) id else mpuId,
                                                    siteId,
                                                    managerName,
                                                    managerContact,
                                                    capacity,
                                                    if (cbuCount.isEmpty()) 0 else cbuCount.toInt(),
                                                    if (bmsCount.isEmpty()) 0 else bmsCount.toInt(),
                                                )

                                            } else {
                                                message =
                                                    context.resources.getString(R.string.required_msg)
                                            }
                                        } else {
                                            message = String.format(
                                                context.resources.getString(
                                                    R.string.duplicate_confirm,
                                                    "MPU ID"
                                                )
                                            )

                                        }

                                        if (message.isNotEmpty()) {
                                            scope.launch {
                                                showSnackBar(scaffoldState = scaffoldState, message = message)
                                            }
                                        }

                                        scope.launch {
                                            mpuViewModel.result.collect { response ->
                                                val result =
                                                    if (mode == Mode.Add) context.resources.getString(
                                                        R.string.register
                                                    ) else context.resources.getString(
                                                        R.string.modify
                                                    )

                                                if (response) {
                                                        showSnackBar(
                                                        scaffoldState = scaffoldState,
                                                            message = String.format(
                                                                context.resources.getString(R.string.mpu_success),
                                                                result
                                                            ),)

                                                    navController.popBackStack()
                                                } else {
                                                    showSnackBar(
                                                        scaffoldState = scaffoldState,
                                                        message = String.format(
                                                            context.resources.getString(R.string.mpu_fail),
                                                            result
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                    }) {
                                    Text(
                                        text = stringResource(id = com.pms.admin.R.string.store),
                                        color = Color.White
                                    )
                                }

                                Spacer(Modifier.width(10.dp))

                                Button(modifier = Modifier.border(
                                    width = 1.dp,
                                    color = Color.Transparent,
                                    shape = RoundedCornerShape(5.dp)
                                ),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                    onClick = {
                                        navController.popBackStack()
                                    }) {
                                    Text(
                                        text = stringResource(id = com.pms.admin.R.string.cancel),
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

@Preview
@Composable
fun MPUAddEditPrev() {

}