package com.pms.admin.model

import com.google.gson.annotations.SerializedName

data class SiteListResult(
    @SerializedName("descr")
    val descr: String = "",

    @SerializedName("mgrid")
    val mgrid: List<String> = emptyList<String>(),

    @SerializedName("mpuid")
    val mpuid:  List<Int> = emptyList<Int>(),

    @SerializedName("site_addr")
    val site_addr: String = "",

    @SerializedName("site_id")
    val site_id: Int = 0,

    @SerializedName("site_name")
    val site_name: String = "",

    @SerializedName("user_id")
    val user_id:  List<String> = emptyList<String>(),
)


data class SiteIDResult(
    @SerializedName("site_id")
    val site_id: String = "",
)

data class SiteInfoResult(
    @SerializedName("site_id")
    val site_id: String = "",

    @SerializedName("site_name")
    val site_name: String = "",

    @SerializedName("site_addr")
    val site_addr: String = "",

    @SerializedName("descr")
    val descr: String = "",
)

data class SiteMPUListResult(
    @SerializedName("mpuid")
    val mpuid: String = "",
)