package com.example.pdf_scanner.ui.component.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.ImageCard
import com.example.pdf_scanner.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ImageViewModel @Inject constructor() : BaseViewModel() {

    var listAll = ArrayList<ImageCard>()

    private var listData = MutableLiveData<Resource<ArrayList<ImageCard>>>()
    val list: LiveData<Resource<ArrayList<ImageCard>>> get() = listData

    private var listDataSelected = MutableLiveData<Resource<ArrayList<ImageCard>>>()
    val listSelected: LiveData<Resource<ArrayList<ImageCard>>> get() = listDataSelected

    fun fetchImage(list: ArrayList<String>) {

        viewModelScope.launch {
            var listImg = ArrayList<ImageCard>()
            for(i in 0 until Math.min(20, list.size)){
                listImg.add(ImageCard(list[i], ""))
            }
            listData.value = Resource.Success(listImg)
            for(i in 0 until list.size) {
                listAll.add(ImageCard(list[i], ""))
            }
        }
    }

    // Every one load more 20 item images
}