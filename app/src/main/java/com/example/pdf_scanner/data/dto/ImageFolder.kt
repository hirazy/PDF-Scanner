package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageFolder(
    var name: String = "",

    var time: String = "",

    var list: ArrayList<String> = ArrayList()
): OBase()