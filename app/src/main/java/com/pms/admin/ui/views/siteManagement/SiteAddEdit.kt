package com.pms.admin.ui.views.siteManagement

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.pms.admin.model.Mode
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.viewModels.SiteViewModel
import com.pms.admin.ui.component.common.RegisterItem
import com.pms.admin.ui.component.menu.Header
import com.pms.admin.ui.component.menu.SidebarMenu
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.theme.ContentLine
import com.pms.admin.ui.theme.ContentsBackground
import com.pms.admin.ui.views.managerManagement.FocusItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SiteAddEdit(
    navController: NavHostController,
    siteId: String,
    siteViewModel: SiteViewModel = viewModel(
        factory = PMSAndroidViewModelFactory(
            PMSAdminApplication.getInstance()
        )
    ),
) {
    val viewModel: MainViewModel = viewModel(LocalContext.current as ComponentActivity)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var id by rememberSaveable { mutableStateOf("") }
    var siteName by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current   //중복체크 후 키보드 감추기

    val focusManager = LocalFocusManager.current
    val (idFocus, nameFocus, addressFocus, descFocus) = FocusRequester.createRefs()     //next text focus

    val window = rememberWindowSize()
    val mode: Mode = if (siteId.isEmpty()) Mode.Add else Mode.Edit


    //site 등록 및 수정 성공,실패 팝업창
    LaunchedEffect(true) {
        siteViewModel.result.collect { response ->
            val result = if (mode == Mode.Add) "등록" else "수정"
            if (response) {

                val job = scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = String.format(
                            context.resources.getString(
                                R.string.register_site_success,
                                result
                            )
                        ),
                        duration = SnackbarDuration.Indefinite,
                    )
                }
                delay(1000)
                job.cancel()

                navController.popBackStack()
            } else {

                scaffoldState.snackbarHostState.showSnackbar(
                    String.format(
                        context.resources.getString(
                            R.string.register_site_fail,
                            result
                        )
                    )
                )
            }
        }
    }

    //site가 생성 될 때, siteId, siteName 서버에서 default 받아오기
    LaunchedEffect(true) {
        if (mode == Mode.Add) siteViewModel.getSitesId()
        siteViewModel.siteId.collect { result ->
            id = result.site_id
            siteName = "Site_${result.site_id}"
        }
    }

    //site 정보 불러오기
    LaunchedEffect(siteId) {
        siteViewModel.getSiteInfo(siteId)

        siteViewModel.siteInfo.collect { site ->

            id = site.site_id
            siteName = site.site_name
            address = site.site_addr
            description = site.descr
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
                    Header(
                        navController,
                        viewModel,
                        stringResource(id = R.string.site_create),
                        R.drawable.add_circle
                    )//아이콘 차후 수정
                else
                    Header(
                        navController,
                        viewModel,
                        stringResource(id = R.string.site_modify),
                        R.drawable.edit
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

                        //site id text
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.site_id),
                                item = if (mode == Mode.Add) id else siteId,
                                required = true,
                                focusItem = FocusItem(idFocus, nameFocus),
                                disabled = true,
                                onTextValueChange = { }
                            )
                        }

                        //site name text
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
                                        Text(text = "*", color = Color.Red)

                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = stringResource(id = R.string.name),
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
                                                    .focusRequester(nameFocus)
                                                    .focusProperties {
                                                        next = addressFocus
                                                    },
                                                value = siteName,
                                                maxLines = 1,
                                                onValueChange = { siteName = it },
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
                                                siteViewModel.checkDuplicatedSiteName(siteName)

                                                scope.launch {
                                                    siteViewModel.checkDuplicatedSiteName.collect { result ->
                                                        val message =
                                                            if (result) context.resources.getString(
                                                                R.string.duplicate_site
                                                            ) else context.resources.getString(R.string.available_site)

                                                        scaffoldState.snackbarHostState.showSnackbar(message)
                                                    }
                                                }
                                            }) {
                                            Text(text = stringResource(id = R.string.duplicate_check), color = Color.White)
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

                        //address text
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.address),
                                item = address,
                                required = false,
                                focusItem = FocusItem(addressFocus, descFocus),
                                onTextValueChange = { address = it }
                            )
                        }

                        //설명 text
                        item {
                            RegisterItem(
                                title = stringResource(id = R.string.description),
                                item = description,
                                required = false,
                                focusItem = FocusItem(descFocus, null),
                                onDone = {
                                    keyboardController?.hide()
                                    siteViewModel.registerSite(
                                        mode,
                                        id,
                                        siteName,
                                        address,
                                        description
                                    )
                                },
                                onTextValueChange = { description = it }
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
                                        siteViewModel.registerSite(
                                            mode,
                                            id,
                                            siteName,
                                            address,
                                            description
                                        )
                                    }) {
                                    Text(text = stringResource(id = R.string.store), color = Color.White)
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
                                    Text(text =stringResource(id = R.string.cancel), color = Color.White)
                                }
                            }
                        }
                    }

                }


            }

        }
    }

}


