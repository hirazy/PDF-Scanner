    package com.example.pdf_scanner.ui.component.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pdf_scanner.*
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.OptionCamera
import com.example.pdf_scanner.ui.base.BaseViewModel
import com.example.pdf_scanner.utils.SingleEvent
import com.flurry.sdk.t
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {

    private var listOptionData = MutableLiveData<Resource<ArrayList<OptionCamera>>>()
    val listOption: LiveData<Resource<ArrayList<OptionCamera>>> get() = listOptionData

    private var toastLiveDataPrivate = MutableLiveData<SingleEvent<String>>()
    val toastLiveData: LiveData<SingleEvent<String>> get() = toastLiveDataPrivate

    init {
        var list = ArrayList<OptionCamera>()
        list.add(OptionCamera(WHITE_BOARD, false))
        list.add(OptionCamera(OCR, false))
        list.add(OptionCamera(SINGLE, true))
        list.add(OptionCamera(BATCH, false))
        list.add(OptionCamera(CARD, false))
        list.add(OptionCamera(QRCODE, false))

        listOptionData.value = Resource.Success(list)
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            toastLiveDataPrivate.value = SingleEvent(msg)
        }
    }

    fun selectItem(position: Int) {
        if(listOptionData.value!!.data?.get(position)!!.isSelected){
            return
        }
        viewModelScope.launch {
            var list = listOptionData.value!!.data
            for(i in 0 until list!!.size){
                list[i].isSelected = i == position
            }
            listOptionData.value = Resource.Success(list)
        }
        when (position) {
            0 -> {
                showToast(TOAST_WHITEBOARD)
            }
            1 -> {
                showToast(TOAST_OCR)
            }
            2 -> {
                showToast(TOAST_SINGLE)
            }
            3 -> {
                showToast(TOAST_BATCH)
            }
            4 -> {
                showToast(TOAST_CARD)
            }
            5 -> {
                showToast(TOAST_OCR)
            }
        }
    }
}