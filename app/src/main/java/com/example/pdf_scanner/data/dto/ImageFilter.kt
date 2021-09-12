package com.example.pdf_scanner.data.dto

import ja.burhanrashid52.photoeditor.PhotoFilter
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageFilter(
    var path: String = "",

    var filter: PhotoFilter,

    var isSelected: Boolean = false
): OBase()