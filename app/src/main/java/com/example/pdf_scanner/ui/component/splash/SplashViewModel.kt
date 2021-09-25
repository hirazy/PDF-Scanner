package com.example.pdf_scanner.ui.component.splash

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
class SplashViewModel @Inject constructor(var dataRepository: DataRepositorySource) :
    BaseViewModel() {

    private var isStartedCamera = MutableLiveData<Resource<Boolean>>()
    val liveStartedCamera: LiveData<Resource<Boolean>> get() = isStartedCamera

    init {
        viewModelScope.launch {
            dataRepository.requestStartCamera().collect {
                when (it) {
                    is Resource.Success -> {
                        isStartedCamera.value = Resource.Success(it.data!!)
                    }
                }
            }
        }
    }

}