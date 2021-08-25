package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
class DataScan (
    var listImg : ArrayList<String>,

    var status: Int = 0,

    var ocrEnabled : Boolean = false
): OBase()