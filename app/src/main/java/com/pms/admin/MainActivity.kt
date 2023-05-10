package com.pms.admin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.pms.admin.domain.util.PMSAndroidViewModelFactory
import com.pms.admin.navigation.nav_graph.SetupNavGraph
import com.pms.admin.ui.MainViewModel
import com.pms.admin.ui.theme.AdminBackground

class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel(
                factory = PMSAndroidViewModelFactory(application)
            )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AdminBackground
                ) {
                      SetupNavGraph(navController, viewModel)
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