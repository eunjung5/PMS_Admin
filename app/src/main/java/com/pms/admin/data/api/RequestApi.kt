package com.pms.admin.data.api

import com.google.gson.annotations.SerializedName
import com.pms.admin.model.*
import com.pms.admin.model.response.*
import org.json.JSONArray
import retrofit2.Response
import retrofit2.http.*

//result data
data class ResponseResult(
    @SerializedName("result")
    val result: Boolean = false,
)

interface AuthRequestApi {
    @FormUrlEncoded
    @POST("login.php")  //login query parameter
    suspend fun loginUser(
        @Field("op") op: String,
        @Field("user_id") userid: String,
        @Field("sha1") sha1: String
    ): Response<UserLoginResult>

    @FormUrlEncoded
    @POST("logout.php")  //logout query parameter
    suspend fun logoutUser(@Field("op") op: String): Response<ResponseResult>

    @FormUrlEncoded
    @POST("register.php")  //사용자 등록
    suspend fun registerUser(
        @Field("op") op: String,
        @Field("user_id") user_id: String,
        @Field("role") role: String,
        @Field("name") name: String,
        @Field("sha1") sha1: String,
        @Field("tel") tel: String
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("check_user.php")  //사용자 중복체크
    suspend fun checkDuplicatedID(@Field("user_id") id: String): Response<ResponseResult>

    @GET("check_auth.php")  //권한check
    suspend fun checkAuthority(): Response<AuthorityResult>

    @FormUrlEncoded
    @POST("check_pw.php")   //관리자 비밀번호 check
    suspend fun checkAdminPassword(
        @Field("user_id") user_id: String,
        @Field("sha1") sha1: String
    ): Response<ResponseResult>
}

interface RequestApi {
    // @Headers("Content-Type: application/json")//header를 추가하는 경우.
    @GET("admin.php")  //관리자 리스트
    suspend fun getManagerList(@Query("work") work: String): Response<List<ManagerListResult>>

    @FormUrlEncoded
    @POST("admin.php")  //사용자 정보 불러오기 for 사용자 수정
    suspend fun getUserInfo(
        @Field("work") work: String,
        @Field("user_id") user_id: String
    ): Response<UserInfoResult>

    @FormUrlEncoded
    @POST("admin.php")  //사용자 수정
    suspend fun updateUser(
        @Field("work") work: String,
        @Field("user_id") user_id: String,
        @Field("role") role: String,
        @Field("name") name: String,
        @Field("tel") tel: String
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("admin.php")  //사용자 비밀번호 초기화
    suspend fun updateUserPassword(
        @Field("work") work: String,
        @Field("user_id") user_id: String,
        @Field("sha1") sha1: String
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("admin.php")  //관리자 작업 조회 리스트
    suspend fun getJobList(
        @Field("work") work: String,
        @Field("user_id") user_id: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("contents") contents: String,
        @Field("page") page: Int
    ): Response<List<JobListResult>>

    @FormUrlEncoded
    @POST("admin.php")  //사용자 삭제
    suspend fun deleteUser(
        @Field("work") work: String,
        @Field("user_id") user_id: String,
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("sites.php")  //site list 불러오기
    suspend fun getSiteList(
        @Field("work") work: String,
    ): Response<List<SiteListResult>>

    @FormUrlEncoded
    @POST("sites.php")  //site id & name 불러오기
    suspend fun getSitesId(
        @Field("work") work: String,
    ): Response<SiteIDResult>

    @FormUrlEncoded
    @POST("sites.php")  //site name 중복체크
    suspend fun checkDuplicatedSiteName(
        @Field("work") work: String,
        @Field("site_name") site_name: String,
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("sites.php")  //site 등록
    suspend fun registerSite(
        @Field("work") work: String,
        @Field("site_id") site_id: String,
        @Field("site_name") site_name: String,
        @Field("site_addr") site_addr: String,
        @Field("descr") descr: String
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("sites.php")  //site 수정 정보 불러오기
    suspend fun getSiteInfo(
        @Field("work") work: String,
        @Field("site_id") site_id: String
    ): Response<SiteInfoResult>

    @FormUrlEncoded
    @POST("sites.php")  //mpu list 불러오기
    suspend fun getMPUList(
        @Field("work") work: String,
        @Field("site_id") site_id: String
    ): Response<List<SiteMPUListResult>>

    @FormUrlEncoded
    @POST("sites.php")  //site - mpu add & delete
    suspend fun setSitesMPUAddDelete(
        @Field("work") work: String,
        @Field("site_id") site_id: String,
        @Field("site_name") site_name: String,
        @Field("mpu_list") mpu_list: JSONArray
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("sites.php")  //site - manager add
    suspend fun getManagerList(
        @Field("work") work: String,
        @Field("site_id") site_id: String
    ): Response<List<SiteManagerListResult>>

    @FormUrlEncoded
    @POST("sites.php")  //site - manager add & delete
    suspend fun setSitesManagerAddDelete(
        @Field("work") work: String,
        @Field("site_id") site_id: String,
        @Field("site_name") site_name: String,
        @Field("mgr_list") mgr_list: JSONArray
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("sites.php")  //site - delete site 정보 받아오기
    suspend fun getDeleteSites(
        @Field("work") work: String,
        @Field("site_id") site_id: Number
    ): Response<List<SiteDeleteInfoResult>>

    @FormUrlEncoded
    @POST("sites.php")  //site delete
    suspend fun deleteSite(
        @Field("work") work: String,
        @Field("site_id") site_id: Number,
        @Field("site_name") site_name: String
    ): Response<ResponseResult>

    @GET("mpu.php")  //관리자 리스트
    suspend fun getMPUList(@Query("work") work: String): Response<List<MPUListResult>>

    @FormUrlEncoded
    @POST("mpu.php")  //mpu id 중복체크
    suspend fun checkDuplicatedMPUID(
        @Field("work") work: String,
        @Field("mpu_id") mpuId: Int
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("mpu.php")  //mpu - site id 리스트 불러오기
    suspend fun getSiteIDList(@Field("work") work: String): Response<List<SiteIDListResult>>

    @FormUrlEncoded
    @POST("mpu.php") //mpu 생성
    suspend fun registerMPUInfo(
        @Field("work") work: String,
        @Field("mpu_id") mpu_id: String,
        @Field("site_id") site_id: String,
        @Field("op_name") op_name: String,
        @Field("op_tel") op_tel: String,
        @Field("capacity") capacity: String,
        @Field("hip1") hip1: Int,
        @Field("hip2") hip2: Int,
        @Field("lip1_1") lip1_1: Int,
        @Field("lip1_2") lip1_2: Int,
        @Field("lip2_1") lip2_1: Int,
        @Field("lip2_2") lip2_2: Int,
        @Field("inv1") inv1: Int,
        @Field("inv2") inv2: Int,
        @Field("pcs1") pcs1: Int,
        @Field("pcs2") pcs2: Int,
        @Field("cbu1") cbu1: Int,
        @Field("cbu2") cbu2: Int,
        @Field("bms1") bms1: Int,
        @Field("bms2") bms2: Int
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("mpu.php") //mpu 정보 불러오기
    suspend fun getMPUInfo(
        @Field("work") work: String,
        @Field("mpu_id") mpu_id: String
    ): Response<MPUInfoResult>

    @FormUrlEncoded
    @POST("mpu.php") //mpu 삭제
    suspend fun deleteMPU(
        @Field("work") work: String,
        @Field("mpu_id") mpu_id: String
    ): Response<ResponseResult>

    @FormUrlEncoded
    @POST("admin.php")  //관리자 작업 조회 all history 리스트
    suspend fun getAllHistory(
        @Field("work") work: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("page") page: Int
    ): Response<List<JobListResult>>

    @FormUrlEncoded
    @POST("admin.php")  //작업자 id list 불러오기
    suspend fun getUserIdList(@Field("work") work: String): Response<List<UserIdListResult>>

    @FormUrlEncoded
    @POST("admin.php")  //관리자별 조회 불러오기
    suspend fun getDateIdSearch(
        @Field("work") work: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String,
        @Field("user_id") user_id: String,
        @Field("page") page: Int
    ): Response<List<UserJobListResult>>

    @FormUrlEncoded
    @POST("admin.php")  //Admin pw 변경
    suspend fun setPasswordChange(
        @Field("work") work: String,
        @Field("user_id") user_id: String,
        @Field("sha1") sha1: String,
        @Field("new_sha1") new_sha1: String
    ): Response<ResponseResult>
}
