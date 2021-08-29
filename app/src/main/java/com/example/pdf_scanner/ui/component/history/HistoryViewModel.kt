package com.example.pdf_scanner.ui.component.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class HistoryViewModel @Inject constructor(var context: CoroutineContext): BaseViewModel(){

    var listData = MutableLiveData<Resource<ArrayList<ImageFolder>>>()
    val listLiveData : LiveData<Resource<ArrayList<ImageFolder>>> get() = listData

    fun fetchData(list: ArrayList<ImageFolder>){
        listData.value = Resource.Success(list)
    }

}