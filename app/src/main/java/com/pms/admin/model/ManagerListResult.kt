package com.pms.admin.model

import com.google.gson.annotations.SerializedName

//manager list  result
data class ManagerListResult(
    @SerializedName("descr")
    val descr: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("role")
    val role: String = "",

    @SerializedName("sites")
    val sites: List<String> ,

    @SerializedName("tel")
    val tel: String = "",

    @SerializedName("user_id")
    val user_id: String = "",
)

data class UserInfoResult(
    @SerializedName("user_id")
    val user_id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("role")
    val role: String = "",

    @SerializedName("tel")
    val tel: String = "",
)

data class AuthorityResult(
    @SerializedName("result")
    val result: String = "",

    @SerializedName("authenticated")
    val authenticated: String = "",

    @SerializedName("user_id")
    val user_id: String = "",
)


data class JobListResult(
    @SerializedName("user_id")
    val user_id: String = "",

    @SerializedName("work_date")
    val work_date: String = "",

    @SerializedName("work_type")
    val work_type: String = "",

    @SerializedName("dscr")
    val dscr: String = "",

    @SerializedName("result")
    val result: Boolean = false,
)

