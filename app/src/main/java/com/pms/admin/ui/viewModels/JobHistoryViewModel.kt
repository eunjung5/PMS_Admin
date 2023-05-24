package com.pms.admin.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pms.admin.MainActivity
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.response.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class JobHistoryViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {

    private val _result = MutableSharedFlow<Boolean>()
    val result: SharedFlow<Boolean> = _result

    private val _jobAllList = MutableSharedFlow<List<JobListResult>>()
    val jobAllList :SharedFlow<List<JobListResult>>  = _jobAllList

    private val _userIdList = MutableSharedFlow<List<UserIdListResult>>()
    val userIdList :SharedFlow<List<UserIdListResult>>  = _userIdList

    private val _jobDataIdList = MutableSharedFlow<List<UserJobListResult>>()
    val jobDataIdList :SharedFlow<List<UserJobListResult>>  = _jobDataIdList

    //관리자 작업조회 리스트 불러오기
    fun getAllHistory(startDate:String, endDate:String, page:Int = 0){
        viewModelScope.launch {
            try {
                val response: Response<List<JobListResult>> =
                    remoteRepository.getAllHistory(startDate, endDate, page)
                response.body()?.let { _jobAllList.emit(it)  }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //작업자 id list 불러오기
    fun getUserIdList(){
        viewModelScope.launch {
            try {
                val response: Response<List<UserIdListResult>> =
                    remoteRepository.getUserIdList()
                response.body()?.let { _userIdList.emit(it)  }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //관리자별 조회
    fun getDateIdSearch(startDate:String, endDate:String, userId:String,page:Int = 0){
        viewModelScope.launch {
            try {
                val response: Response<List<UserJobListResult>> =
                    remoteRepository.getDateIdSearch(startDate, endDate, userId,page)
                response.body()?.let { _jobDataIdList.emit(it)  }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }
}