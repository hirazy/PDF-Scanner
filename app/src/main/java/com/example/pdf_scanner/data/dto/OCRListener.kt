package com.example.pdf_scanner.data.dto

interface OCRListener {

    fun detectOCR(text: String)
}