package com.pms.admin.ui.component.menu

import android.graphics.drawable.Icon
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.pms.admin.MainActivity
import com.pms.admin.WindowType
import com.pms.admin.rememberWindowSize


@Composable
fun RowScope.MainMenuItem(
    menuName: String,
    icon : Painter,
    url: String,
    onNavigate : (String)-> Unit = {},

) {
    val window = rememberWindowSize()

    Column(
        modifier = Modifier
            .weight(1f)
            .height(if (window.height == WindowType.Medium) 200.dp else 100.dp)
            .background(color = Color(0xFF38373d))
            .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(5.dp))
            .clickable {
                Log.d(MainActivity.TAG, "MainMenuItem click ")
                onNavigate(url)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ){
        Icon(
            painter = icon,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .height(if (window.height == WindowType.Medium) 100.dp else 50.dp)
                .width(if (window.height == WindowType.Medium) 100.dp else 50.dp)
                .padding(top = 10.dp)

        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = menuName,
            color = Color.White,
            fontSize = if(window.height == WindowType.Medium)  20.sp else 15.sp)

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000 )
@Composable
fun DefaultPreview() {
   // MainMenuItem("관리자 관리","",painterResource(id = R.drawable.main_user))
}