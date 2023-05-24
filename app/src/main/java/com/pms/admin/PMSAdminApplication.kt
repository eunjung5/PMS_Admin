package com.pms.admin

import android.app.Application
import android.content.Context

class PMSAdminApplication: Application() {
    companion object{
        private lateinit var  pmsAdminApplication: PMSAdminApplication
        fun getInstance() : PMSAdminApplication = pmsAdminApplication
        fun getContext(): Context = pmsAdminApplication.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        pmsAdminApplication = this
    }
}