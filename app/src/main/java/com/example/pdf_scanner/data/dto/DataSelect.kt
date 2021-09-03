package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataSelect (
    var list: ArrayList<ImageFolder>
    ): OBase()