package com.example.pdf_scanner.data.dto

import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderSelect(
    var isSelected: Boolean = false,

    var folder: ImageFolder
): OBase()