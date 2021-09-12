package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataDetailText (
    var filePath: String = ""
        ): OBase()