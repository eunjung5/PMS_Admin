package com.pms.admin.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import com.pms.admin.MainActivity
import com.pms.admin.PMSAdminApplication
import com.pms.admin.data.repository.RemoteRepositoryImpl
import com.pms.admin.domain.repository.RemoteRepository

class NetworkManager {

    companion object {
        private val remoteRepository: RemoteRepository= RemoteRepositoryImpl(PMSAdminApplication.getInstance())

        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(ConnectivityManager::class.java)
            val network: Network = connectivityManager.activeNetwork ?: return false
            val actNetwork: NetworkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        }

        suspend fun checkAuthority():Boolean {
            val check = remoteRepository.checkAuthority()
            var result = true

            Log.e(MainActivity.TAG,"check = ${check.body()} ")
            check.body()?.let { response ->
                result =  response.result != "false"
            }

            return result
        }
    }

}