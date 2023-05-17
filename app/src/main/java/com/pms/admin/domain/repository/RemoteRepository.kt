package com.pms.admin.domain.repository

import com.pms.admin.data.api.ResponseResult
import com.pms.admin.model.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Query

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
    suspend fun checkDuplicatedSiteName(siteName:String): Response<ResponseResult>
    suspend fun registerSite(work:String,siteId:String, siteName:String, siteAddr:String, descr:String): Response<ResponseResult>
    suspend fun getSiteInfo( site_id:String): Response<SiteInfoResult>
    suspend fun getMPUList(mode:Mode,site_id:String):Response<List<SiteMPUListResult>>
}