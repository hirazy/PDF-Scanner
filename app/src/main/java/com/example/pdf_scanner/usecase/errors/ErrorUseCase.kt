package com.example.pdf_scanner.usecase.errors

import com.example.pdf_scanner.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
