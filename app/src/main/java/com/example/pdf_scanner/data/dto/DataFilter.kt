package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataFilter (
    var isFilterAll: Boolean = false,

    var path: String = ""
        ): OBase()