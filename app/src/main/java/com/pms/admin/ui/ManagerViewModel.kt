package com.pms.admin.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pms.admin.MainActivity
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.JobListResult
import com.pms.admin.model.ManagerListResult
import kotlinx.coroutines.launch
import retrofit2.Response

class ManagerViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {
    private val _managerList = mutableStateOf<List<ManagerListResult>>(emptyList())
    val managerList: State<List<ManagerListResult>> = _managerList

    //관리자 관리 리스트 받아오기
    fun getManagerList() {
        Log.d(MainActivity.TAG, "getManagerList")

        viewModelScope.launch {
            try {
                val response: Response<List<ManagerListResult>> =
                    remoteRepository.getManagerList(work = "get_list")
                Log.d(MainActivity.TAG, "userInfo : ${response.body()}")

                response.body()?.let { _managerList.value = it }

            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")

            }

        }
    }



}