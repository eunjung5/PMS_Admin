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
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.*
import com.pms.admin.model.data.Mode
import com.pms.admin.model.response.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Response

class SiteViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {

    private val _checkAuth = MutableStateFlow<Boolean>(true)
    val checkAuth: StateFlow<Boolean> = _checkAuth

    private val _result = MutableSharedFlow<Boolean>()
    val result: SharedFlow<Boolean> = _result

    private val _updateList = mutableStateOf<Boolean>(false)
    val updateList: State<Boolean> = _updateList

    private val _siteList = mutableStateOf<List<SiteListResult>>(emptyList())
    val siteList: State<List<SiteListResult>> = _siteList

    private val _siteId = MutableSharedFlow<SiteIDResult>()
    val siteId: SharedFlow<SiteIDResult> = _siteId

    private val _checkDuplicatedSiteName = MutableSharedFlow<Boolean>()
    val checkDuplicatedSiteName: SharedFlow<Boolean> = _checkDuplicatedSiteName

    private val _siteInfo = MutableSharedFlow<SiteInfoResult>()
    val siteInfo: SharedFlow<SiteInfoResult> = _siteInfo

    private val _mpuList = MutableSharedFlow<List<SiteMPUListResult>>()
    val mpuList: SharedFlow<List<SiteMPUListResult>> = _mpuList

    private val _managerList = MutableSharedFlow<List<SiteManagerListResult>>()
    val managerList: SharedFlow<List<SiteManagerListResult>> = _managerList

    private val _siteDeleteInfo = MutableSharedFlow<SiteDeleteInfoResult>()
    val siteDeleteInfo: SharedFlow<SiteDeleteInfoResult> = _siteDeleteInfo

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val check = remoteRepository.checkAuthority()
            check.body()?.let { response ->
                if (response.result == "false") {
                    _checkAuth.emit(false)
                }
            }
        }
    }

    //사이트 리스트 불러오기
    fun getSiteList() {
        viewModelScope.launch {
            try {
                val response: Response<List<SiteListResult>> =
                    remoteRepository.getSiteList()
                response.body()?.let { _siteList.value = it }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    fun getSitesId() {

        viewModelScope.launch {
            try {
//                val check = remoteRepository.checkAuthority()
//                check.body()?.let { response ->
//                    if(response.result == "false"){
//                        _checkAuth.emit(false)
//                    }
//                }
                val response: Response<SiteIDResult> =
                    remoteRepository.getSitesId()
                Log.e(MainActivity.TAG, "getSitesId = ${response.body()}")
                response.body()?.let { result -> _siteId.emit(result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site id 중복체크
    fun checkDuplicatedSiteName(siteName: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.checkDuplicatedSiteName(siteName)
                response.body()?.let { response ->
                    _checkDuplicatedSiteName.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site 등록 및 수정 : Mode로 구분
    fun registerSite(
        mode: Mode,
        siteId: String,
        siteName: String,
        siteAddr: String,
        descr: String
    ) {
        val command = when (mode) {
            Mode.Add -> "set_sites_create"
            Mode.Edit -> "set_sites_modify"
            else -> "invalid_command"
        }
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.registerSite(command, siteId, siteName, siteAddr, descr)
                response.body()?.let { response ->
                    _result.emit(response.result)
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //Site 수정 정보 받아오기
    fun getSiteInfo(siteId: String) {

        viewModelScope.launch {
            try {
                val response: Response<SiteInfoResult> =
                    remoteRepository.getSiteInfo(siteId)
                response.body()?.let { response ->
                    _siteInfo.emit(response)
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu list 불러오기
    fun getMPUList(mode: Mode, siteId: String) {
        viewModelScope.launch {
            try {
                val response: Response<List<SiteMPUListResult>> =
                    remoteRepository.getMPUList(mode, siteId)

                response.body()?.let { _mpuList.emit(it) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site - mpu 추가 & 삭제하기
    fun setSitesMPUAddDelete(mode: Mode, siteId: String, siteName: String, mpuList: JSONArray) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.setSitesMPUAddDelete(mode, siteId, siteName, mpuList)

                response.body()?.let { _result.emit(it.result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu list 불러오기
    fun getManagerList(mode: Mode, siteId: String) {
        viewModelScope.launch {
            try {
                val response: Response<List<SiteManagerListResult>> =
                    remoteRepository.getManagerList(mode, siteId)

                response.body()?.let { _managerList.emit(it) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site - manager 추가 & 삭제하기
    fun setSitesManagerAddDelete(
        mode: Mode,
        siteId: String,
        siteName: String,
        managerList: JSONArray
    ) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.setSitesManagerAddDelete(mode, siteId, siteName, managerList)

                response.body()?.let { _result.emit(it.result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site - delete 시 정보 받아오기
    fun getDeleteSites(siteId: Number) {
        viewModelScope.launch {
            try {
                val response: Response<List<SiteDeleteInfoResult>> =
                    remoteRepository.getDeleteSites(siteId)

                response.body()?.let { _siteDeleteInfo.emit(it[0]) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //site 삭제
    fun deleteSite(siteId: Number, siteName: String) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.deleteSite(siteId, siteName)

                response.body()?.let { _result.emit(it.result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //list update
    fun setListUpdate(update: Boolean) {
        _updateList.value = update
    }
}