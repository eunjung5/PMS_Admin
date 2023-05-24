package com.pms.admin.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pms.admin.MainActivity
import com.pms.admin.MainActivity.Companion.TAG
import com.pms.admin.data.api.ResponseResult
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.response.*
import com.pms.admin.model.data.MPUConfiguration
import com.pms.admin.model.data.Mode
import com.pms.admin.model.response.MPUListResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class MPUViewModel(
    application: Application,
    private val remoteRepository: RemoteRepository,
) : AndroidViewModel(application) {

    private val _result = MutableSharedFlow<Boolean>()
    val result: SharedFlow<Boolean> = _result

    private val _updateList = mutableStateOf<Boolean>(false)
    val updateList: State<Boolean> = _updateList

    private val _mpuList = MutableSharedFlow<List<MPUListResult>>()
    val mpuList: SharedFlow<List<MPUListResult>> = _mpuList

    private val _siteList = MutableSharedFlow<SiteIDListResult>()
    val siteList: SharedFlow<SiteIDListResult> = _siteList

    private val _checkDuplicatedMPUId = MutableSharedFlow<Boolean>()
    val checkDuplicatedMPUId: SharedFlow<Boolean> = _checkDuplicatedMPUId

    //MPU 구성요소 데이터들
    private val _mpuConfiguration = mutableStateListOf(
        MPUConfiguration(title = "HIP1", key = "hip1", checked = true),
        MPUConfiguration(title = "LIP1.1", key = "lip1_1", checked = true),
        MPUConfiguration(title = "LIP2.1", key = "lip2_1"),
        MPUConfiguration(title = "INV1", key = "inv1", checked = true),
        MPUConfiguration(title = "PCS1", key = "pcs1"),
        MPUConfiguration(title = "CBU1.0", key = "cbu1"),
        MPUConfiguration(title = "BMS1.0", key = "bms1"),
        MPUConfiguration(title = "HIP2", key = "hip2"),
        MPUConfiguration(title = "LIP1.2", key = "lip1_2"),
        MPUConfiguration(title = "LIP2.2", key = "lip2_2"),
        MPUConfiguration(title = "INV2", key = "inv2"),
        MPUConfiguration(title = "PCS2", key = "pcs2"),
        MPUConfiguration(title = "CBU2.0", key = "cbu2", enable = false),
        MPUConfiguration(title = "BMS2.0", key = "bms2", enable = false),
    )
    val mpuConfiguration: List<MPUConfiguration> = _mpuConfiguration

    private val _mpuInfo = MutableSharedFlow<MPUInfoResult>()
    val mpuInfo: SharedFlow<MPUInfoResult> = _mpuInfo

    //mpu list 불러오기
    fun getMPUList() {
        viewModelScope.launch {
            try {
                val response: Response<List<MPUListResult>> =
                    remoteRepository.getMPUList()
                response.body()?.let { _mpuList.emit(it)  }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu id 중복 체크
    fun checkDuplicatedMPUID(mpuId: String) {

        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.checkDuplicatedMPUID(mpuId.toInt())
                response.body()?.let { _checkDuplicatedMPUId.emit(it.result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu 생성시, 유효한 site id list 불러오기
    fun getSiteIDList() {
        viewModelScope.launch {
            try {
                val response: Response<List<SiteIDListResult>> =
                    remoteRepository.getSiteIDList()

                Log.e(TAG, response.body().toString())
                response.body()?.let { _siteList.emit(it[0]) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu 구성 checkbox update
    fun setMPUConfigurationChecked(index: Int, checked: Boolean) {
        _mpuConfiguration[index] = _mpuConfiguration[index].copy(checked = checked)
    }


    //mpu 생성하기
    fun registerMPUInfo(
        mode: Mode,
        mpu_id: String,
        site_id: String,
        op_name: String,
        op_tel: String,
        capacity: String,
        cbu_count: Int = 0,
        bms_count: Int = 0
    ) {
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.registerMPUInfo(
                        mode, mpu_id, site_id, op_name, op_tel, capacity,
                        getMPUConfigurationValue("hip1"),
                        getMPUConfigurationValue("hip2"),
                        getMPUConfigurationValue("lip1_1"),
                        getMPUConfigurationValue("lip1_2"),
                        getMPUConfigurationValue("lip2_1"),
                        getMPUConfigurationValue("lip2_2"),
                        getMPUConfigurationValue("inv1"),
                        getMPUConfigurationValue("inv2"),
                        getMPUConfigurationValue("pcs1"),
                        getMPUConfigurationValue("pcs2"),
                        getMPUConfigurationValue("cbu1", cbu_count),
                        getMPUConfigurationValue("cbu2", cbu_count),
                        getMPUConfigurationValue("bms1", bms_count),
                        getMPUConfigurationValue("bms2", bms_count),
                    )
                response.body()?.let { _result.emit(it.result) }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    private fun getMPUConfigurationValue(key: String): Int {
        val config: MPUConfiguration = _mpuConfiguration.filter { config -> config.key == key }[0]
        return if (config.checked) 1 else 0
    }


    private fun getMPUConfigurationValue(key: String, count: Int): Int {
        val config: MPUConfiguration = _mpuConfiguration.filter { config -> config.key == key }[0]
        return if (config.checked) count else 0
    }

    //mpu 정보 불러오기
    fun getMPUInfo(mpuId: String) {
        viewModelScope.launch {
            try {
                val response: Response<MPUInfoResult> =
                    remoteRepository.getMPUInfo(mpuId)

                Log.e(TAG, response.body().toString())
                response.body()?.let {
                    _mpuInfo.emit(it)

                    _mpuConfiguration[0] = _mpuConfiguration[0].copy(checked = it.hip1 != 0)
                    _mpuConfiguration[1] = _mpuConfiguration[1].copy(checked = it.lip1_1 != 0)
                    _mpuConfiguration[2] = _mpuConfiguration[2].copy(checked = it.lip2_1 != 0)
                    _mpuConfiguration[3] = _mpuConfiguration[3].copy(checked = it.inv1 != 0)
                    _mpuConfiguration[4] = _mpuConfiguration[4].copy(checked = it.pcs1 != 0)
                    _mpuConfiguration[5] = _mpuConfiguration[5].copy(checked = it.cbu1 != 0)
                    _mpuConfiguration[6] = _mpuConfiguration[6].copy(checked = it.bms1 != 0)
                    _mpuConfiguration[7] = _mpuConfiguration[7].copy(checked = it.hip2 != 0)
                    _mpuConfiguration[8] = _mpuConfiguration[8].copy(checked = it.lip1_2 != 0)
                    _mpuConfiguration[9] = _mpuConfiguration[9].copy(checked = it.lip2_2 != 0)
                    _mpuConfiguration[10] = _mpuConfiguration[10].copy(checked = it.inv2 != 0)
                    _mpuConfiguration[11] = _mpuConfiguration[11].copy(checked = it.pcs2 != 0)
                    _mpuConfiguration[12] = _mpuConfiguration[12].copy(checked = it.cbu2 != 0)
                    _mpuConfiguration[13] = _mpuConfiguration[13].copy(checked = it.bms2 != 0)


                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    //mpu 삭제
    fun deleteMPU(mpuId:String){
        viewModelScope.launch {
            try {
                val response: Response<ResponseResult> =
                    remoteRepository.deleteMPU(mpuId)
                response.body()?.let {
                    _result.emit(it.result)
                }
            } catch (e: Exception) {
                Log.d(MainActivity.TAG, "Exception : $e")
            }
        }
    }

    fun setListUpdate(update:Boolean){
        _updateList.value = update
    }
}