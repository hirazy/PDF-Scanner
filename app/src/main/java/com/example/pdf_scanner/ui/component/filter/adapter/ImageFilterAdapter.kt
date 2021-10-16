package com.example.pdf_scanner.ui.component.filter.adapter

import android.content.Context
import android.net.Uri
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageFilter
import com.example.pdf_scanner.databinding.CardImageFilterBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId
import ja.burhanrashid52.photoeditor.PhotoEditor
import java.io.File

@LayoutId(R.layout.card_image_filter)
class ImageFilterAdapter(var e: RecyclerItemListener,
                         var uri: Uri , var context: Context): AdapterBase<ImageFilter, CardImageFilterBinding>(e) {
    override fun bindView(itemBinding: BaseHolder<CardImageFilterBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]

        var mPhotoEditor = PhotoEditor.Builder(context, itemBinding.itemBinding.imageFilter)
            .setPinchTextScalable(true)
            .build()

        itemBinding.itemBinding.imageFilter.source.setImageURI(uri)

        mPhotoEditor.setFilterEffect(listData[position].filter)
    }
}