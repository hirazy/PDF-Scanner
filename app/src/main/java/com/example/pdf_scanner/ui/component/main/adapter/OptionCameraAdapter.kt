package com.example.pdf_scanner.ui.component.main.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.OptionCamera
import com.example.pdf_scanner.databinding.CardOptionBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_option)
class OptionCameraAdapter(event: RecyclerItemListener):
    AdapterBase<OptionCamera, CardOptionBinding>(event) {
    override fun bindView(itemBinding: BaseHolder<CardOptionBinding>, position: Int) {

        itemBinding.itemBinding.o = listData[position]
    }
}