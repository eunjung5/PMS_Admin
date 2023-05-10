package com.pms.admin

import android.app.Application

class PMSAdminApplication: Application() {
    companion object{
        private lateinit var  pmsAdminApplication: PMSAdminApplication
        fun getInstance() : PMSAdminApplication = pmsAdminApplication
    }

    override fun onCreate() {
        super.onCreate()
        pmsAdminApplication = this
    }
}