package com.example.pdf_scanner.ui.component.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.data.DataRepositorySource
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource) :
    BaseViewModel() {
    private var isStartedCamera = MutableLiveData<Resource<Boolean>>()
    val liveStartedCamera: LiveData<Resource<Boolean>> get() = isStartedCamera

    private var textSizeEdit = MutableLiveData<Resource<Int>>()
    val liveTextSizeEdit: LiveData<Resource<Int>> get() = textSizeEdit


    init {
        viewModelScope.launch {
            dataRepositorySource.requestStartCamera().collect {
                when (it) {
                    is Resource.Success -> {
                        isStartedCamera.value = Resource.Success(it.data!!)
                    }
                }
            }

            dataRepositorySource.requestTextSize().collect {
                when (it) {
                    is Resource.Success -> {
                        textSizeEdit.value = Resource.Success(it.data!!)
                    }
                }
            }
        }
    }

    fun changeStartCamera(isEnabled: Boolean) {
        viewModelScope.launch {
            dataRepositorySource.cacheStartCamera(isEnabled).collect {

            }
        }
    }

    fun setTextSize(textSize: Int) {
        viewModelScope.launch {
            dataRepositorySource.cacheTextSize(textSize).collect {

            }
        }
    }

}