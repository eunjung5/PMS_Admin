package com.pms.admin.data.api

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pms.admin.MainActivity.Companion.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

private const val SESSION_PREFERENCES = "com.pms.admin.session_manager.SESSION_PREFERENCES"
private const val SESSION_EXPIRY_TIME = "com.pms.admin.session_manager.SESSION_EXPIRY_TIME"
private const val SESSION_USER_ID = "com.pms.admin.session_manager.SESSION_USER_ID"

object SessionManagerUtil {
    fun startUserSession(context: Context, expiresIn: Int){
        val calendar = Calendar.getInstance()
        val userLoggedInTime = calendar.time
        calendar.time = userLoggedInTime
        calendar.add(Calendar.SECOND, expiresIn)
        val expiryTime = calendar.time
        val editor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit()
        editor.putLong(SESSION_EXPIRY_TIME, expiryTime.time)
        editor.apply()

    }

    fun isSessionActive(currentTime: Date,context: Context) : Boolean {
        val sessionExpiresAt = Date(getExpiryDateFromPreferences(context)!!)
        Log.d(TAG, "isSessionActive = ${sessionExpiresAt}")
        return !currentTime.after(sessionExpiresAt)
    }

    private fun getExpiryDateFromPreferences(context: Context) : Long? {
        return context.getSharedPreferences(SESSION_PREFERENCES, 0).getLong(SESSION_EXPIRY_TIME, 0)
    }

    fun endUserSession(context: Context) {
        clearStoredData(context)
    }
    private fun clearStoredData(context: Context) {
        val editor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit()
        editor.clear()
        editor.apply()
    }

    fun setUserID(context: Context,userId:String){
        val editor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit()
        editor.putString(SESSION_USER_ID, userId)
        editor.apply()
    }
}
