package com.pms.admin.model

import com.google.gson.annotations.SerializedName

//사용자 권한 및 connect result
data class UserLoginResult(
    @SerializedName("authenticated")
    val authenticated: String = "",

    @SerializedName("result")
    val result: Boolean = false,

)
