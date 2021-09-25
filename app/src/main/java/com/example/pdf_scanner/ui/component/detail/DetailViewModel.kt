package com.example.pdf_scanner.ui.component.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.ImageDetail
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor() : BaseViewModel() {

    private var listDataImage = MutableLiveData<Resource<ArrayList<ImageDetail>>>()
    val listImage: LiveData<Resource<ArrayList<ImageDetail>>> get() = listDataImage

    init {
    }

    fun fetchData(list: ArrayList<String>) {
        var listTemp = ArrayList<ImageDetail>()
        for(i in 0 until list.size){
            listTemp.add(ImageDetail(list[i]))
        }
        listDataImage.value = Resource.Success(listTemp)
    }
}