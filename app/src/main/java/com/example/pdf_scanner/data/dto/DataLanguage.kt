package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataLanguage(
    var language: String = ""
): OBase()