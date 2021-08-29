package com.example.pdf_scanner.ui.component.ocr

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
class OCRViewModel @Inject constructor(var dataRepositorySource: DataRepositorySource): BaseViewModel() {

    private var listDataLanguage = MutableLiveData<Resource<ArrayList<LanguageOCR>>>()
    val listLanguage : LiveData<Resource<ArrayList<LanguageOCR>>> get() = listDataLanguage

    init {
    }

    fun fetchLanguage(list: ArrayList<LanguageOCR>) {
        viewModelScope.launch {
            dataRepositorySource.requestLanguageOCR().collect {
                when (it) {
                    is Resource.Success -> {
                        var tmp = it.data
                        for (i in 0 until list.size) {
                            if (tmp!!.contains(list[i].name)) {
                                list[i].isEnabled = true
                            }
                        }
                        listDataLanguage.value = Resource.Success(list)
                    }
                }
            }
        }
    }



}