package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageDetail(
    var path : String = ""
): OBase()