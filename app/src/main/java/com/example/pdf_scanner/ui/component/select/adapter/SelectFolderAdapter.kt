package com.example.pdf_scanner.ui.component.select.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.FolderSelect
import com.example.pdf_scanner.databinding.CardFolderSelectBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_folder_select)
class SelectFolderAdapter(e: RecyclerItemListener): AdapterBase<FolderSelect, CardFolderSelectBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardFolderSelectBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]
    }
}