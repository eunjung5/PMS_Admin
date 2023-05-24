package com.pms.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.navigation.nav_graph.SetupNavGraph
import com.pms.admin.ui.component.common.CustomAlertDialog
import com.pms.admin.ui.viewModels.MainViewModel
import com.pms.admin.ui.theme.AdminBackground
import com.pms.admin.ui.views.siteManagement.DeleteSite
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel(
                factory = PMSAndroidViewModelFactory(application)
            )
//            var finishDialog by remember { mutableStateOf(false) } //서버에 권한 체크해서, 계정 문제 있을때, 종료 되도록 팝업
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AdminBackground
            ) {

                //server에 connect 유효한지 check
//                lifecycleScope.launch {
//                    viewModel.checkAuth.collect { result ->
//                        Log.w(TAG, "check auth = ${result}")
//                        if (!result) {
//                            //finish()
//                            finishDialog = true
//                        }
//                    }
//                }
                SetupNavGraph(navController)


            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

//        setContent{
//            Surface(){
//                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
//            }
//        }


    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        const val TAG = "PMS"
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

}