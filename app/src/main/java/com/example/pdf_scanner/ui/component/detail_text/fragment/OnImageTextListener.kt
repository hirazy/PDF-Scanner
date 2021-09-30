package com.example.pdf_scanner.ui.component.detail_text.fragment

interface OnImageTextListener {
    fun onPrint()

    fun toAlbum()

    fun onDelete()

    fun onSign()

    fun onSave()
}

interface OnTextListener{
    fun onDelete()

    fun onOCR(text: String)

    fun onSave()

    fun onSign()
}