package com.example.pdf_scanner.ui.component.history.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.example.pdf_scanner.R
import com.example.pdf_scanner.RESOLUTION_HEIGHT
import com.example.pdf_scanner.RESOLUTION_WIDTH
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.databinding.CardFolderBinding
import com.example.pdf_scanner.ui.base.AdapterFolder
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecycleFolderListener
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.LayoutId
import java.io.ByteArrayOutputStream
import java.io.File

@LayoutId(R.layout.card_folder)
class FolderAdapter(var e: RecycleFolderListener) :
    AdapterFolder<ImageFolder, CardFolderBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardFolderBinding>, position: Int) {

        itemBinding.itemBinding.o = listData[position]

        itemBinding.itemBinding.cardFolder.setOnClickListener {
            e.onItemSelected(position, listData[position])
            itemBinding.itemBinding.swipeFolder.close(true)
        }

        itemBinding.itemBinding.layoutDeleteFolder.setOnClickListener {
            e.onItemDelete(position, listData[position])
            itemBinding.itemBinding.swipeFolder.close(true)
        }

        itemBinding.itemBinding.layoutMoreFolder.setOnClickListener {
            e.onItemMore(position, listData[position])
            itemBinding.itemBinding.swipeFolder.close(true)
        }

        itemBinding.itemBinding.layoutReNameFolder.setOnClickListener {
            e.onItemRename(position, listData[position])
            itemBinding.itemBinding.swipeFolder.close(true)
        }
    }
}
