package com.example.pdf_scanner.ui.component.image.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageCard
import com.example.pdf_scanner.databinding.CardImageSelectedBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_image_selected)
class CardImageSelectedAdapter(var e: RecyclerItemListener) :
    AdapterBase<ImageCard, CardImageSelectedBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardImageSelectedBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]

        itemBinding.itemBinding.btnCancelSelected.setOnClickListener {
            e.onOption(position, listData[position])
        }
    }
}