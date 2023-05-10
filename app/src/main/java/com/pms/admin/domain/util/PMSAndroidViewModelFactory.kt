package com.pms.admin.domain.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pms.admin.data.repository.RemoteRepositoryImpl
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.ui.MainViewModel

class PMSAndroidViewModelFactory (
    private val application: Application,
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(application),
) : ViewModelProvider.AndroidViewModelFactory(application){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java))
        {
            return MainViewModel(application = application, remoteRepository) as T
        }
        return super.create(modelClass)
    }
}