package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
class DataMain(
    var isDeleted : Boolean = false
): OBase()