package com.example.pdf_scanner.ui.component.history.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.databinding.CardFolderBinding
import com.example.pdf_scanner.ui.base.AdapterFolder
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecycleFolderListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_folder)
class FolderAdapter(var e: RecycleFolderListener) : AdapterFolder<ImageFolder, CardFolderBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardFolderBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]

        itemBinding.itemBinding.cardFolder.setOnClickListener {
            e.onItemSelected(position, listData[position])
        }

        itemBinding.itemBinding.layoutDeleteFolder.setOnClickListener {
            e.onItemDelete(position, listData[position])
        }

        itemBinding.itemBinding.layoutMoreFolder.setOnClickListener {
            e.onItemMore(position, listData[position])
        }

        itemBinding.itemBinding.layoutReNameFolder.setOnClickListener {
            e.onItemRename(position, listData[position])
        }
    }
}