package com.pms.admin.domain.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pms.admin.data.repository.RemoteRepositoryImpl
import com.pms.admin.domain.repository.RemoteRepository
import com.pms.admin.ui.viewModels.*

class PMSAndroidViewModelFactory(
    private val application: Application,
    private val remoteRepository: RemoteRepository = RemoteRepositoryImpl(application),
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application = application, remoteRepository) as T
        } else if (modelClass.isAssignableFrom(ManagerViewModel::class.java)) {
            return ManagerViewModel(application = application, remoteRepository) as T
        } else if (modelClass.isAssignableFrom(SiteViewModel::class.java)) {
            return SiteViewModel(application = application, remoteRepository) as T
        } else if (modelClass.isAssignableFrom(MPUViewModel::class.java)) {
            return MPUViewModel(application = application, remoteRepository) as T
        }else if (modelClass.isAssignableFrom(JobHistoryViewModel::class.java)) {
            return JobHistoryViewModel(application = application, remoteRepository) as T
        }
        return super.create(modelClass)
    }
}