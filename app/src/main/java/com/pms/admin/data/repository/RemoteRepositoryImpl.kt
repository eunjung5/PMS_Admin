package com.pms.admin.data.repository

import android.app.Application
import com.pms.admin.data.api.AuthRequestApi
import com.pms.admin.data.api.RequestApi
import com.pms.admin.data.api.ResponseResult
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.*
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

const val AUTH_BASE_URL = "http://10.1.10.2/php/members/"
const val BASE_URL = "http://10.1.10.2/php/members/admin/"

//retrofit singleton object - login and logout
object AuthRetrofitInstance {
    val api = Retrofit.Builder()
        .client(RetrofitInstance.client) //OkHttpClient 연결
        .baseUrl(AUTH_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance(): Retrofit {
        return api
    }
}

object RetrofitInstance {
     val client = OkHttpClient.Builder()
//         .addInterceptor{ chain: Interceptor.Chain ->
//             val original = chain.request()
//             chain.proceed(original.newBuilder().apply {
//                 addHeader("user_id", "admin2")
//             }.build())
//         }
//         .addInterceptor(HttpLoggingInterceptor().apply {
//             level = HttpLoggingInterceptor.Level.BODY
//         })
        .cookieJar(JavaNetCookieJar(CookieManager())) //쿠키매니저 연결
        .build()

    val api = Retrofit.Builder()
         .client(client) //OkHttpClient 연결
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance(): Retrofit {
        return api
    }
}

class RemoteRepositoryImpl(application: Application) : RemoteRepository {
    private val authApi = AuthRetrofitInstance.getInstance().create(AuthRequestApi::class.java)
    private val api = RetrofitInstance.getInstance().create(RequestApi::class.java)

    override suspend fun loginUser(
        userid: String,
        sha1: String
    ): Response<UserLoginResult> {
        return authApi.loginUser(op="login", userid, sha1)
    }

    override suspend fun logoutUser(): Response<ResponseResult> {
        return authApi.logoutUser(op="logout")
    }

    override suspend fun getManagerList(): Response<List<ManagerListResult>> {
        return api.getManagerList(work = "get_list")
    }

    override suspend fun registerUser(
        op: String,
        user_id: String,
        role: String,
        name: String,
        sha1: String,
        tel: String
    ): Response<ResponseResult> {
        return authApi.registerUser(op, user_id, role, name, sha1, tel)
    }

    override suspend fun checkDuplicatedID(id: String): Response<ResponseResult> {
        return authApi.checkDuplicatedID(id)
    }


    override suspend fun checkAuthority(): Response<AuthorityResult>{
        return authApi.checkAuthority()
    }

    override suspend fun checkAdminPassword(user_id: String, sha1: String): Response<ResponseResult>{
        return authApi.checkAdminPassword(user_id,sha1)
    }

    override suspend fun updateUser(
        user_id: String,
        role: String,
        name: String,
        tel: String
    ): Response<ResponseResult>{
        return api.updateUser(work="set_modify", user_id, role, name, tel)
    }

    override suspend fun getUserInfo( user_id: String): Response<UserInfoResult> {
        return api.getUserInfo(work = "get_modify", user_id = user_id)
    }

    override suspend fun updateUserPassword(
        user_id: String,
        sha1: String
    ): Response<ResponseResult> {
        return api.updateUserPassword(work="set_reset",  user_id = user_id, sha1 = sha1)
    }

    override suspend fun getJobList(
        user_id: String,
        start_date: String,
        end_date: String,
        contents: String,
        page: Int
    ): Response<List<JobListResult>> {
        return api.getJobList(
            work = "get_date_contents_search",
            user_id = user_id,
            start_date = start_date,
            end_date = end_date,
            contents = contents,
            page = page
        )
    }

    override suspend fun deleteUser( user_id: String): Response<ResponseResult>{
        return api.deleteUser("set_del",user_id)
    }

    override suspend fun getSiteList(): Response<List<SiteListResult>>{
        return api.getSiteList("get_list")
    }

    override suspend fun getSitesId(): Response<SiteIDResult>{
        return api.getSitesId("get_sites_id")
    }
    override suspend fun checkDuplicatedSiteName(siteName:String): Response<ResponseResult>{
        return api.checkDuplicatedSiteName("get_sites_check",siteName)
    }

    override suspend fun registerSite(work:String, site_id:String, site_name:String, site_addr:String, descr:String): Response<ResponseResult>{
        return api.registerSite(work,site_id, site_name, site_addr, descr)
    }

    override suspend fun getSiteInfo(site_id: String): Response<SiteInfoResult> {
        return api.getSiteInfo("get_modify", site_id)
    }

    override suspend fun getMPUList(mode:Mode,site_id:String):Response<List<SiteMPUListResult>>{
        val work = if(mode == Mode.Add) "get_mpu_add" else "get_mpu_delete"
        return api.getMPUList(work,site_id)
    }

}