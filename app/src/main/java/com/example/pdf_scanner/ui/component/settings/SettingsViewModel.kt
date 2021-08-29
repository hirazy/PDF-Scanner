package com.example.pdf_scanner.ui.component.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(): BaseViewModel() {
    var isStartedCamera = MutableLiveData<Resource<Boolean>>()
    val liveStartedCamera : LiveData<Resource<Boolean>> get() = isStartedCamera

    init{

    }

    fun changeStartCamera(){

    }


}