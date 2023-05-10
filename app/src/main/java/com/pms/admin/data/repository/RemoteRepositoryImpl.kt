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
        op: String,
        userid: String,
        sha1: String
    ): Response<UserLoginResult> {
        return authApi.loginUser(op, userid, sha1)
    }

    override suspend fun logoutUser(op: String): Response<ResponseResult> {
        return authApi.logoutUser(op)
    }

    override suspend fun getManagerList( work: String): Response<List<ManagerListResult>> {
        return api.getManagerList(work)
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
        work: String,
        user_id: String,
        role: String,
        name: String,
        tel: String
    ): Response<ResponseResult>{
        return api.updateUser(work, user_id, role, name, tel)
    }


    override suspend fun getUserInfo(work: String, user_id: String): Response<UserInfoResult> {
        return api.getUserInfo(work = "get_modify", user_id = user_id)
    }

    override suspend fun updateUserPassword(
        work: String,
        user_id: String,
        sha1: String
    ): Response<ResponseResult> {
        return api.updateUserPassword(work = "set_reset", user_id = user_id, sha1 = sha1)
    }

    override suspend fun getJobList(
        work: String,
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

    override  suspend fun deleteUser( work: String, user_id: String): Response<ResponseResult>{
        return api.deleteUser(work="set_del",user_id)
    }
}