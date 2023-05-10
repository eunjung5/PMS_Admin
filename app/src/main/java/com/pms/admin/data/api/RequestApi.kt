package com.pms.admin.data.api

import com.google.gson.annotations.SerializedName
import com.pms.admin.model.*
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
    suspend fun checkAdminPassword( @Field("user_id") user_id: String, @Field("sha1")sha1: String): Response<ResponseResult>
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
}
