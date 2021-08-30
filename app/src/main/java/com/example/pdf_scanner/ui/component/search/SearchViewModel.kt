package com.example.pdf_scanner.ui.component.search

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
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource): BaseViewModel(){

    private var listAll = ArrayList<ImageFolder>()

    private var listDataFolder = MutableLiveData<Resource<ArrayList<ImageFolder>>>()
    val listFolder: LiveData<Resource<ArrayList<ImageFolder>>> get() = listDataFolder

    init {

    }

    fun fetchData(list: ArrayList<ImageFolder>){
        listAll = list
        listDataFolder.value = Resource.Success(list)
    }

    fun search(key: String){
        viewModelScope.launch {
            var listTemp = ArrayList<ImageFolder>()

            for(i in 0 until listAll!!.size){
                if(listAll[i].name.contains(key)){
                    listTemp.add(listAll[i])
                }
            }
            listDataFolder.value = Resource.Success(listTemp)
        }
    }
}