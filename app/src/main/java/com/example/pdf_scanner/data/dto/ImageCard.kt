package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageCard(
    var path: String = "",

    var countSelected: String = "",

): OBase()