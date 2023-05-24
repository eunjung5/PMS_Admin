package com.pms.admin.ui.viewModels

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
import com.pms.admin.model.data.Error
import com.pms.admin.model.response.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private val _checkAuth = MutableStateFlow<Boolean>(true)
    val checkAuth: StateFlow<Boolean> = _checkAuth

    private val _loginUser = mutableStateOf<UserLoginResult>(UserLoginResult())
    val loginUser: State<UserLoginResult> = _loginUser

    private val _logInSession = MutableSharedFlow<Boolean>()
    val logInSession: SharedFlow<Boolean> = _logInSession

    private val _error = mutableStateOf<String>("")
    val error: State<String> = _error

    private val _logoutResult = MutableSharedFlow<Boolean>()
    val logoutResult: SharedFlow<Boolean> = _logoutResult

    private val _checkAdminPasswordResult = MutableSharedFlow<Boolean>()
    val checkAdminPasswordResult: SharedFlow<Boolean> = _checkAdminPasswordResult

    private val _changePassword = MutableSharedFlow<Boolean>()
    val changePassword: SharedFlow<Boolean> = _changePassword

    init{
        checkAuth()
    }

    //사용자 로그인
    fun login(id: String, password: String) {
        viewModelScope.launch {
            try {
                val userInfo: Response<UserLoginResult> =
                    remoteRepository.loginUser(userid = id, sha1 = password)

                if (userInfo.body()?.result == true) {
                    _loginUser.value = userInfo.body()!!
                    SessionManagerUtil.setUserID(context, id)
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
                val response: Response<ResponseResult> = remoteRepository.logoutUser()
                response.body()?.result?.let {
                    SessionManagerUtil.endUserSession(context)
                    _logoutResult.emit(it)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Exception : $e")
            }
        }
    }

    //관리자 비밀번호 check
    fun checkAdminPassword(user_id: String, sha1: String) {

        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.checkAdminPassword(user_id, sha1)
                Log.e(MainActivity.TAG, "checkAdminPassword = ${response.body()}")
                response.body()?.let { response ->
                    _checkAdminPasswordResult.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //권한 check
    fun checkAuth() {
        viewModelScope.launch {
            val check = remoteRepository.checkAuthority()
            check.body()?.let { response ->
                if (response.result == "false") {
                    _checkAuth.emit(false)
                }
            }
        }
    }

    //관리자 비밀번호 변경
    fun setPasswordChange(userId:String, sha1:String, newSha1:String){
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.setPasswordChange(userId, sha1,newSha1)
                Log.e(MainActivity.TAG, "setPasswordChange = ${response.body()}")
                response.body()?.let { response ->
                    _changePassword.emit(response.result)
                    if(response.result) logout()
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

}