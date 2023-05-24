package com.pms.admin.model.data

/**
 * MPUConfiguration
 * title: MPU 구성 checkbox label name
 * key: server request value key
 * checked: checkbox default checked
 * enable: checkbox enabled
 */
data class MPUConfiguration(
    var title: String,
    val key: String,
    var checked: Boolean = false,
    val enable: Boolean = true
)