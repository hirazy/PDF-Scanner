package com.example.pdf_scanner.ui.base.listener

import com.example.pdf_scanner.data.dto.OBase

/**
 * Created by AhmedEltaher
 */

interface RecyclerItemListener {
    fun onItemSelected(index: Int, data: OBase)

    fun onOption(index: Int, data: OBase)
}
