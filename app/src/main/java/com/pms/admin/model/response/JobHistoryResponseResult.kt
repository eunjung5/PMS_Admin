package com.pms.admin.model.response

import com.google.gson.annotations.SerializedName

data class UserIdListResult(
    @SerializedName("user_id")
    val user_id: String = "",
)

data class UserJobListResult(
    @SerializedName("user_id")
    val user_id: String = "",

    @SerializedName("work_date")
    val work_date: String = "",

    @SerializedName("work_type")
    val work_type: String = "",
)