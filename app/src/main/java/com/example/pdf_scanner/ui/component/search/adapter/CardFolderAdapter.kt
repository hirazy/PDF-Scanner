package com.example.pdf_scanner.ui.component.search.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.databinding.CardFolderBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_folder)
class CardFolderAdapter(var e: RecyclerItemListener) : AdapterBase<ImageFolder, CardFolderBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardFolderBinding>, position: Int) {

    }
}