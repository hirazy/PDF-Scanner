package com.example.pdf_scanner.data.dto

import ja.burhanrashid52.photoeditor.PhotoFilter
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataResultFilter(
    var filter: PhotoFilter,

    var isFilterAll : Boolean = false
): OBase()