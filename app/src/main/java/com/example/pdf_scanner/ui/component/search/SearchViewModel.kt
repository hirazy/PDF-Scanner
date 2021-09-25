package com.example.pdf_scanner.ui.component.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.data.DataRepositorySource
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.ui.base.BaseViewModel
import com.flurry.sdk.fo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class SearchViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource): BaseViewModel(){

    private var listAll = ArrayList<ImageFolder>()

    private var listDataFolder = MutableLiveData<Resource<ArrayList<ImageFolder>>>()
    val listFolder: LiveData<Resource<ArrayList<ImageFolder>>> get() = listDataFolder

    init {

    }

    fun fetchData(list: ArrayList<ImageFolder>){
        listAll = ArrayList(list)
        listDataFolder.value = Resource.Success(list)
    }

    fun search(key: String){
        Log.e("search", key + listAll.size)
        var listData = listAll
        viewModelScope.launch {
            var listTemp = ArrayList<ImageFolder>()
            var keySearch = key.toLowerCase()
            for(i in 0 until listData!!.size){
                Log.e("listAll[i].name", keySearch)
                if(listData[i].name.toLowerCase().contains(keySearch)){
                    listTemp.add(listData[i])
                }
            }
            listDataFolder.value = Resource.Success(listTemp)
        }
    }
}