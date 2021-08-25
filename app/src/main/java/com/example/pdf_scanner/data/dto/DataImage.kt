package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class DataImage(
    var status: Int = 0
): OBase()
