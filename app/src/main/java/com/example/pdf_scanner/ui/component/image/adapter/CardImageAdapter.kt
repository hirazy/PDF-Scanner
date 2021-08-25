package com.example.pdf_scanner.ui.component.image.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageCard
import com.example.pdf_scanner.databinding.CardImageBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId


@LayoutId(R.layout.card_image)
class CardImageAdapter(event: RecyclerItemListener) :
    AdapterBase<ImageCard, CardImageBinding>(event) {
    override fun bindView(itemBinding: BaseHolder<CardImageBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]
    }
}