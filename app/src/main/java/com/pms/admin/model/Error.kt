package com.pms.admin.model

data class Error(
    var reason: String = "",
) {

    fun getErrorMessage(): String {
        return when (this.reason) {
            "ConnectException" -> "네트워크 오류입니다."
            "Not Matched" -> "아이디나 비밀번호가 맞지 않습니다."
            else -> "처리가 정상적으로 되지 않았습니다."
        }
    }
}