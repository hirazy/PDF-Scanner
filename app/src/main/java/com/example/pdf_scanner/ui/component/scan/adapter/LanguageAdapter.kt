package com.example.pdf_scanner.ui.component.scan.adapter

import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.DataLanguage
import com.example.pdf_scanner.databinding.CardLanguageSelectedBinding
import com.example.pdf_scanner.ui.base.AdapterBase
import com.example.pdf_scanner.ui.base.BaseHolder
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.utils.LayoutId

@LayoutId(R.layout.card_language_selected)
class LanguageAdapter(e: RecyclerItemListener): AdapterBase<DataLanguage, CardLanguageSelectedBinding>(e){
    override fun bindView(itemBinding: BaseHolder<CardLanguageSelectedBinding>, position: Int) {
        itemBinding.itemBinding.o = listData[position]
    }
}