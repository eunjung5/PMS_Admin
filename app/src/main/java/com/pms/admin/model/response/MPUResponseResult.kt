package com.pms.admin.model.response

import com.google.gson.annotations.SerializedName

data class MPUListResult(
    @SerializedName("mpu_id")
    val mpu_id: Int = 0,

    @SerializedName("site_name")
    val site_name: String="",

    @SerializedName("composition")
    val composition:  List<String> = emptyList<String>(),
)

data class MPUInfoResult(
    @SerializedName("mpu_id")
    val mpu_id: String="",

    @SerializedName("site_id")
    val site_id: String="",

    @SerializedName("op_name")
    val op_name: String="",

    @SerializedName("op_tel")
    val op_tel: String="",

    @SerializedName("capacity")
    val capacity: String="",

    @SerializedName("hip1")
    val hip1: Int=0,

    @SerializedName("hip2")
    val hip2: Int=0,

    @SerializedName("lip1_1")
    val lip1_1: Int=0,

    @SerializedName("lip1_2")
    val lip1_2: Int=0,

    @SerializedName("lip2_1")
    val lip2_1: Int=0,

    @SerializedName("lip2_2")
    val lip2_2: Int=0,

    @SerializedName("inv1")
    val inv1: Int=0,

    @SerializedName("inv2")
    val inv2: Int=0,

    @SerializedName("pcs1")
    val pcs1: Int=0,

    @SerializedName("pcs2")
    val pcs2:Int=0,

    @SerializedName("cbu1")
    val cbu1:Int=0,

    @SerializedName("cbu2")
    val cbu2: Int=0,

    @SerializedName("bms1")
    val bms1: Int=0,

    @SerializedName("bms2")
    val bms2: Int=0,

)