package com.example.pdf_scanner.ui.component.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.data.DataRepositorySource
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HistoryViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource) :
    BaseViewModel() {

    private var listData = MutableLiveData<Resource<ArrayList<ImageFolder>>>()
    val listLiveData: LiveData<Resource<ArrayList<ImageFolder>>> get() = listData

    private var startCamera = MutableLiveData<Resource<Boolean>>()
    val liveStartCamera: LiveData<Resource<Boolean>> get() = startCamera

    init {
        viewModelScope.launch {
            dataRepositorySource.requestStartCamera().collect {
                when (it) {
                    is Resource.Success -> {
                        startCamera.value = Resource.Success(it.data!!)
                    }
                }
            }
        }
    }

    fun fetchData(list: ArrayList<ImageFolder>) {
        listData.value = Resource.Success(list)
    }
}