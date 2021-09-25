package com.example.pdf_scanner.ui.component.detail.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageDetail
import com.example.pdf_scanner.databinding.CardImageDetailBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_image_detail)
class CardImageDetail(var e: RecyclerItemListener) : AdapterBase<ImageDetail, CardImageDetailBinding>(e){
    override fun bindView(itemBinding: BaseHolder<CardImageDetailBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]
    }
}