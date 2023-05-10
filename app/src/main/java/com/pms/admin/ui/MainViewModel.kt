package com.pms.admin.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pms.admin.MainActivity
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.data.api.ResponseResult
import com.pms.admin.data.api.SessionManagerUtil
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _loginUser = mutableStateOf<UserLoginResult>(UserLoginResult())
    val loginUser: State<UserLoginResult> = _loginUser

    private val _logInSession = MutableSharedFlow<Boolean>()
    val logInSession: SharedFlow<Boolean> = _logInSession

    private val _error = mutableStateOf<String>("")
    val error: State<String> = _error

    private val _logoutResult = MutableSharedFlow<Boolean>()
    val logoutResult: SharedFlow<Boolean> = _logoutResult

    private val _managerList = mutableStateOf<List<ManagerListResult>>(emptyList())
    val managerList: State<List<ManagerListResult>> = _managerList

    private val _registerUserResult = MutableSharedFlow<Boolean>()
    val registerUserResult: SharedFlow<Boolean> = _registerUserResult

    private val _checkDuplicatedId = MutableSharedFlow<Boolean>()
    val checkDuplicatedId: SharedFlow<Boolean> = _checkDuplicatedId

    private val _userInfo = MutableSharedFlow<UserInfoResult>()
    val userInfo: SharedFlow<UserInfoResult> = _userInfo

    private val _result = MutableSharedFlow<Boolean>()
    val result: SharedFlow<Boolean> = _result

    private val _jobList = mutableStateOf<List<JobListResult>>(emptyList())
    val jobList: State<List<JobListResult>> = _jobList

    private val _checkAdminPasswordResult = MutableSharedFlow<Boolean>()
    val checkAdminPasswordResult: SharedFlow<Boolean> = _checkAdminPasswordResult

    private val _updateList = mutableStateOf<Boolean>(false)
    val updateList: State<Boolean> = _updateList

    //사용자 로그인
    fun login(id: String, password: String) {
        viewModelScope.launch {
            try {
                val userInfo: Response<UserLoginResult> =
                    remoteRepository.loginUser(op = "login", userid = id, sha1 = password)

                if (userInfo.body()?.result == true) {
                    _loginUser.value = userInfo.body()!!
                    SessionManagerUtil.setUserID(context,id)
                    _logInSession.emit(true)

                } else {
                    _logInSession.emit(false)
                    _error.value = Error("Not Matched").getErrorMessage().toString()
                }
            } catch (e: Exception) {
                _logInSession.emit(false)
                _error.value = Error("ConnectException").getErrorMessage().toString()
            }
        }
    }

    //사용자 로그아웃
    fun logout() {

        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> = remoteRepository.logoutUser(op = "logout")
                response.body()?.result?.let {
                    SessionManagerUtil.endUserSession(context)
                    _logoutResult.emit(it)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //관리자 관리 리스트 받아오기
    fun getManagerList() {

        viewModelScope.launch {
            try {
                //remoteRepository.checkAuthority()

                val response: Response<List<ManagerListResult>> =
                    remoteRepository.getManagerList(work = "get_list")
                response.body()?.let { _managerList.value = it }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 등록
    fun registerUser( user_id: String, role: String, name: String, sha1: String, tel: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> = remoteRepository.registerUser(
                    op = "register",
                    user_id,
                    role,
                    name,
                    sha1,
                    tel
                )

                response.body()?.let { response ->
                    Log.e(TAG, "registerUser = ${response.result}")
                    _registerUserResult.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 중복체크
    fun checkDuplicatedId(id: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> = remoteRepository.checkDuplicatedID(id)
                response.body()?.let { response ->
                    _checkDuplicatedId.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 정보 불러오기
    fun getUserInfo(user_id: String) {
        viewModelScope.launch {
            try {
                val response: Response<UserInfoResult> =
                    remoteRepository.getUserInfo(work = "get_modify", user_id = user_id)
                response.body()?.let { response ->
                    _userInfo.emit(response)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 수정
    fun updateUser( user_id: String, role: String, name: String, tel: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> = remoteRepository.updateUser(
                    work = "set_modify",
                    user_id,
                    role,
                    name,
                    tel
                )

                response.body()?.let { response ->
                    _registerUserResult.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 비밀번호 reset
    fun updateUserPassword(user_id: String, sha1: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.updateUserPassword("set_reset", user_id, sha1)
                response.body()?.let { response ->
                    _result.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //관리자 작업 조회 리스트 불러오기
    fun getJobList(
        work: String = "get_date_contents_search",
        user_id: String,
        start_date: String,
        end_date: String,
        contents: String,
        page: Int
    ) {
        Log.d(TAG, "getJobList")
        viewModelScope.launch {
            try {
                val response: Response<List<JobListResult>> =
                    remoteRepository.getJobList(
                        work = work,
                        user_id = user_id,
                        start_date = start_date,
                        end_date = end_date,
                        contents = contents,
                        page = page
                    )
                Log.d(MainActivity.TAG, "userInfo : ${response.body()}")

                response.body()?.let { _jobList.value = it }

            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")

            }

        }
    }

    //관리자 비밀번호 check
    fun checkAdminPassword(user_id: String, sha1: String) {

        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.checkAdminPassword( user_id, sha1)
                Log.e(TAG,"checkAdminPassword = ${ response.body()}")
                response.body()?.let { response ->
                    _checkAdminPasswordResult.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //사용자 삭제
    fun deleteUser(user_id:String){
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> = remoteRepository.deleteUser("set_del",user_id)
                Log.e(TAG,"deleteUser = ${ response.body()}")
                response.body()?.let { response ->
                    _result.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //list update
    fun setListUpdate(update:Boolean){
        _updateList.value = update
    }
}