package com.pms.admin.ui.component.table

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pms.admin.model.ManagerListResult
import com.pms.admin.ui.theme.MenuBackground

@Composable
fun CustomTable(
    dataList : List<ManagerListResult>,
    headers : List<String>,
    weights : List<Float>,
) {

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)){
        item{
            Row(Modifier.background(MenuBackground)){
                for(i in  0 until headers.size )
                {
                    TableCell(text = headers[i], weight = weights[i])
                }

            }
        }

        if(dataList.isEmpty()){
            item{
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = "내용 없음", weight = 1F)
                }
            }
        }

        items(dataList){
            val (descr,name,role,sites,tel,user_id) = it

            val site = sites.joinToString()

            Row(Modifier.fillMaxWidth()){
                TableCell(text = user_id, weight = weights[0])
                TableCell(text = name, weight = weights[1])
                TableCell(text = role, weight = weights[2])
                TableCell(text = tel, weight = weights[3])
                TableCell(text = site, weight = weights[4])
            }
        }
    }
}