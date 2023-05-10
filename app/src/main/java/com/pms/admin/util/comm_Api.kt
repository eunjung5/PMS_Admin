package com.pms.admin.util

import android.util.Base64
import java.io.IOException
import java.security.MessageDigest

@Throws(Exception::class)
fun computeSHAHash(str: String): String {
    var digest: String = ""
    digest = try {
        //암호화
        val sh = MessageDigest.getInstance("SHA-1") // SHA-256 해시함수를 사용
        sh.update(str.toByteArray()) // str의 문자열을 해싱하여 sh에 저장
        val byteData = sh.digest() // sh 객체의 다이제스트를 얻는다.

        //얻은 결과를 string으로 변환
        val sb = StringBuffer()
        for (i in byteData.indices) {
            sb.append(Integer.toString((byteData[i].toInt() and 0xff) + 0x100, 16).substring(1))
        }
        sb.toString()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        return ""
    }
    return digest
}
