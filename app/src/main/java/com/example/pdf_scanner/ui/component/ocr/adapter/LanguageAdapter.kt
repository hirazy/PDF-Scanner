package com.example.pdf_scanner.ui.component.ocr.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.LanguageOCR
import com.example.pdf_scanner.databinding.CardLanguageBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_language)
class LanguageAdapter(e: RecyclerItemListener): AdapterBase<LanguageOCR, CardLanguageBinding> (e) {
    override fun bindView(itemBinding: BaseHolder<CardLanguageBinding>, position: Int) {

    }
}