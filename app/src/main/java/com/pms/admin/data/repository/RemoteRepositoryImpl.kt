package com.pms.admin.data.repository

import android.app.Application
import com.pms.admin.data.api.AuthRequestApi
import com.pms.admin.data.api.RequestApi
import com.pms.admin.data.api.ResponseResult
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.model.*
import com.pms.admin.model.data.Mode
import com.pms.admin.model.response.*
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import org.json.JSONArray
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
        return authApi.loginUser(op = "login", userid, sha1)
    }

    override suspend fun logoutUser(): Response<ResponseResult> {
        return authApi.logoutUser(op = "logout")
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


    override suspend fun checkAuthority(): Response<AuthorityResult> {
        return authApi.checkAuthority()
    }

    override suspend fun checkAdminPassword(
        user_id: String,
        sha1: String
    ): Response<ResponseResult> {
        return authApi.checkAdminPassword(user_id, sha1)
    }

    override suspend fun updateUser(
        user_id: String,
        role: String,
        name: String,
        tel: String
    ): Response<ResponseResult> {
        return api.updateUser(work = "set_modify", user_id, role, name, tel)
    }

    override suspend fun getUserInfo(user_id: String): Response<UserInfoResult> {
        return api.getUserInfo(work = "get_modify", user_id = user_id)
    }

    override suspend fun updateUserPassword(
        user_id: String,
        sha1: String
    ): Response<ResponseResult> {
        return api.updateUserPassword(work = "set_reset", user_id = user_id, sha1 = sha1)
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

    override suspend fun deleteUser(user_id: String): Response<ResponseResult> {
        return api.deleteUser("set_del", user_id)
    }

    override suspend fun getSiteList(): Response<List<SiteListResult>> {
        return api.getSiteList("get_list")
    }

    override suspend fun getSitesId(): Response<SiteIDResult> {
        return api.getSitesId("get_sites_id")
    }

    override suspend fun checkDuplicatedSiteName(siteName: String): Response<ResponseResult> {
        return api.checkDuplicatedSiteName("get_sites_check", siteName)
    }

    override suspend fun registerSite(
        work: String,
        site_id: String,
        site_name: String,
        site_addr: String,
        descr: String
    ): Response<ResponseResult> {
        return api.registerSite(work, site_id, site_name, site_addr, descr)
    }

    override suspend fun getSiteInfo(site_id: String): Response<SiteInfoResult> {
        return api.getSiteInfo("get_modify", site_id)
    }

    override suspend fun getMPUList(
        mode: Mode,
        site_id: String
    ): Response<List<SiteMPUListResult>> {
        val work = if (mode == Mode.Add) "get_mpu_add" else "get_mpu_del"
        return api.getMPUList(work, site_id)
    }


    override suspend fun setSitesMPUAddDelete(
        mode: Mode,
        site_id: String,
        site_name: String,
        mpu_list: JSONArray
    ): Response<ResponseResult> {
        val work = if (mode == Mode.Add) "set_sites_mpu_add" else "set_sites_mpu_del"
        return api.setSitesMPUAddDelete(work, site_id, site_name, mpu_list)
    }

    override suspend fun getManagerList(
        mode: Mode,
        site_id: String
    ): Response<List<SiteManagerListResult>> {
        val work = if (mode == Mode.Add) "get_users_add" else "get_users_del"
        return api.getManagerList(work, site_id)
    }

    override suspend fun setSitesManagerAddDelete(
        mode: Mode,
        site_id: String,
        site_name: String,
        mgr_list: JSONArray
    ): Response<ResponseResult> {
        val work = if (mode == Mode.Add) "set_sites_admin_add" else "set_sites_admin_del"
        return api.setSitesManagerAddDelete(work, site_id, site_name, mgr_list)
    }

    override suspend fun getDeleteSites(site_id: Number): Response<List<SiteDeleteInfoResult>> {
        return api.getDeleteSites("get_del_sites", site_id)
    }

    override suspend fun deleteSite(site_id: Number, site_name: String): Response<ResponseResult> {
        return api.deleteSite("set_sites_delete", site_id, site_name)
    }

    override suspend fun getMPUList(): Response<List<MPUListResult>> {
        return api.getMPUList("get_mpu_list")
    }

    override suspend fun checkDuplicatedMPUID(mpuId: Int): Response<ResponseResult> {
        return api.checkDuplicatedMPUID(work = "get_mpu_id_check", mpuId)
    }

    override suspend fun getSiteIDList(): Response<List<SiteIDListResult>> {
        return api.getSiteIDList(work = "get_mpu_create")
    }

    override suspend fun registerMPUInfo(
        mode: Mode,
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
    ): Response<ResponseResult> {
        val work = if (mode == Mode.Add) "set_mpu_create" else "set_mpu_modify"
        return api.registerMPUInfo(
            work = work,
            mpu_id = mpu_id,
            site_id = site_id,
            op_name = op_name,
            op_tel = op_tel,
            capacity = capacity,
            hip1 = hip1,
            hip2 = hip2,
            lip1_1 = lip1_1,
            lip1_2 = lip1_2,
            lip2_1 = lip2_1,
            lip2_2 = lip2_2,
            inv1 = inv1,
            inv2 = inv2,
            pcs1 = pcs1,
            pcs2 = pcs2,
            cbu1 = cbu1,
            cbu2 = cbu2,
            bms1 = bms1,
            bms2 = bms2
        )
    }

    override suspend fun getMPUInfo(mpu_id: String): Response<MPUInfoResult>{
        return api.getMPUInfo(work="get_mpu_modify", mpu_id= mpu_id)
    }

    override suspend fun deleteMPU(mpu_id:String): Response<ResponseResult> {
        return api.deleteMPU(work = "set_mpu_delete", mpu_id = mpu_id)
    }

    override suspend fun getAllHistory(start_date: String,end_date: String,page: Int): Response<List<JobListResult>>{
        return api.getAllHistory(work="get_all_history",start_date=start_date, end_date=end_date, page= page)
    }

    override  suspend fun getUserIdList(): Response<List<UserIdListResult>>{
        return api.getUserIdList(work="get_id_list")
    }

    override suspend fun getDateIdSearch(start_date: String,end_date: String,user_id:String,page: Int): Response<List<UserJobListResult>>{
        return api.getDateIdSearch(work="get_date_id_search",start_date=start_date, end_date=end_date, user_id= user_id,page= page)
    }

    override suspend fun setPasswordChange(user_id: String, sha1: String,newSha1:String): Response<ResponseResult>{
        return api.setPasswordChange(work="set_pw_change",user_id = user_id, sha1= sha1,new_sha1=newSha1 )
    }
}