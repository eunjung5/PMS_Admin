package com.pms.admin.ui.component.table

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.pms.admin.ui.theme.ContentLine

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
) {
    Text(
        text = text,
        modifier = Modifier
            .border(1.dp, ContentLine)
            .weight(weight)
            .height(50.dp)
            .padding(10.dp),
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 15.sp,//MaterialTheme.typography.subtitle1.fontSize,
        maxLines = 1,

        )
}