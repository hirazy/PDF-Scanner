package com.example.pdf_scanner.ui.base.listener

import com.example.pdf_scanner.data.dto.OBase

interface RecycleFolderListener {
    fun onItemSelected(index: Int, data: OBase)

    fun onItemDelete(index: Int, data: OBase)

    fun onItemRename(index: Int, data: OBase)

    fun onItemMore(index: Int, data: OBase)
}