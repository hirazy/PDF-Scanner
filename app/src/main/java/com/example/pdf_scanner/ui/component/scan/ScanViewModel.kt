package com.example.pdf_scanner.ui.component.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.data.DataRepositorySource
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.LanguageOCR
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource): BaseViewModel(){

    private var listDataLanguage = MutableLiveData<Resource<ArrayList<String>>>()
    val listLanguage : LiveData<Resource<ArrayList<String>>> get() = listDataLanguage

    init {
        fetchLanguage()
    }

    fun fetchLanguage(){
        viewModelScope.launch {
            dataRepositorySource.requestLanguageOCR().collect {
                when(it){
                    is Resource.Success ->{
                        var tmp = it.data
                        listDataLanguage.value = Resource.Success(ArrayList<String>(tmp!!.toList()))
                    }
                }
            }
        }
    }
}