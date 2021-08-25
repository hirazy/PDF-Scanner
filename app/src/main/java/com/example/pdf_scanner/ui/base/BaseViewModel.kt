package com.example.pdf_scanner.ui.base

import androidx.lifecycle.ViewModel
import com.example.pdf_scanner.usecase.errors.ErrorManager
import javax.inject.Inject


/**
 * Created by Nguyen Manh Tien
 */


abstract class  BaseViewModel : ViewModel() {
    /**Inject Singleton ErrorManager
     * Use this errorManager to get the Errors
     */
    @Inject
    lateinit var errorManager: ErrorManager
}
