package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataOCR (
    var list : ArrayList<LanguageOCR>
        ):OBase()