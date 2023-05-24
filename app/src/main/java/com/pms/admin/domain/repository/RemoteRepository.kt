package com.pms.admin.domain.repository

import com.pms.admin.data.api.ResponseResult
import com.pms.admin.model.*
import com.pms.admin.model.data.Mode
import org.json.JSONArray
import retrofit2.Response
import com.pms.admin.model.response.*
import retrofit2.http.Field

//Remote Data Source Repository
interface RemoteRepository {
    //user login
    suspend fun loginUser(
        userid: String,
        sha1: String
    ): Response<UserLoginResult>

    //user logout
    suspend fun logoutUser(): Response<ResponseResult>

    //get manager list
    suspend fun getManagerList(): Response<List<ManagerListResult>>

    //사용자 등록
    suspend fun registerUser(
        op: String,
        user_id: String,
        role: String,
        name: String,
        sha1: String,
        tel: String
    ): Response<ResponseResult>

    //id 중복체크
    suspend fun checkDuplicatedID(id: String): Response<ResponseResult>

    //사용자 정보 불러오기
    suspend fun getUserInfo(user_id: String): Response<UserInfoResult>

    //사용자 수정
    suspend fun updateUser(
        user_id: String,
        role: String,
        name: String,
        tel: String
    ): Response<ResponseResult>

    //사용자 비밀번호 초기화
    suspend fun updateUserPassword(
        user_id: String,
        sha1: String
    ): Response<ResponseResult>

    //관리자 작업 조회 리스트
    suspend fun getJobList(
        user_id: String,
        start_date: String,
        end_date: String,
        contents: String,
        page: Int
    ): Response<List<JobListResult>>

    suspend fun checkAuthority(): Response<AuthorityResult>
    suspend fun checkAdminPassword(user_id: String, sha1: String): Response<ResponseResult>
    suspend fun deleteUser(user_id: String): Response<ResponseResult>
    suspend fun getSiteList(): Response<List<SiteListResult>>
    suspend fun getSitesId(): Response<SiteIDResult>
    suspend fun checkDuplicatedSiteName(siteName: String): Response<ResponseResult>
    suspend fun registerSite(
        work: String,
        siteId: String,
        siteName: String,
        siteAddr: String,
        descr: String
    ): Response<ResponseResult>

    suspend fun getSiteInfo(site_id: String): Response<SiteInfoResult>
    suspend fun getMPUList(mode: Mode, site_id: String): Response<List<SiteMPUListResult>>

    suspend fun setSitesMPUAddDelete(
        mode: Mode,
        site_id: String,
        site_name: String,
        mpu_list: JSONArray
    ): Response<ResponseResult>

    suspend fun getManagerList(mode: Mode, site_id: String): Response<List<SiteManagerListResult>>

    suspend fun setSitesManagerAddDelete(
        mode: Mode,
        site_id: String,
        site_name: String,
        mgr_list: JSONArray
    ): Response<ResponseResult>

    suspend fun getDeleteSites(site_id: Number): Response<List<SiteDeleteInfoResult>>
    suspend fun deleteSite(site_id: Number, site_name: String): Response<ResponseResult>
    suspend fun getMPUList(): Response<List<MPUListResult>>
    suspend fun checkDuplicatedMPUID(mpuId: Int): Response<ResponseResult>
    suspend fun getSiteIDList(): Response<List<SiteIDListResult>>
    suspend fun registerMPUInfo(
        mode:Mode,
        mpu_id: String,
        site_id: String,
        op_name: String,
        op_tel: String,
        capacity: String,
        hip1: Int,
        hip2: Int,
        lip1_1: Int,
        lip1_2: Int,
        lip2_1: Int,
        lip2_2: Int,
        inv1: Int,
        inv2: Int,
        pcs1: Int,
        pcs2: Int,
        cbu1: Int,
        cbu2: Int,
        bms1: Int,
        bms2: Int
    ): Response<ResponseResult>

    suspend fun getMPUInfo(mpu_id: String): Response<MPUInfoResult>

    suspend fun deleteMPU(mpu_id:String): Response<ResponseResult>

    suspend fun getAllHistory(start_date: String,end_date: String,page: Int): Response<List<JobListResult>>

    suspend fun getUserIdList(): Response<List<UserIdListResult>>

    suspend fun getDateIdSearch(start_date: String,end_date: String,user_id:String,page: Int): Response<List<UserJobListResult>>

    suspend fun setPasswordChange(user_id: String, sha1: String,newSha1:String): Response<ResponseResult>
}