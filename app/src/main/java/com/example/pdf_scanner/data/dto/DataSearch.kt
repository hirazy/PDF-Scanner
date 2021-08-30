package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataSearch(
    var list: ArrayList<ImageFolder>
): OBase()