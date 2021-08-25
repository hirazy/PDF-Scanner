package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageOCR (
    var name: String =  "",

    var isEnabled: Boolean = false
): OBase()