package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
class ImageFile (
    var path: String = "",

    var name: String = ""
): OBase()