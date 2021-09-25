package com.example.pdf_scanner.ui.component.detail.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.pdf_scanner.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomShare(val e: BottomShareEvent): BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.dialog_bottom_share, container, false)
        var layoutSharePDF = view.findViewById<LinearLayout>(R.id.layoutSharePDF)
        layoutSharePDF.setOnClickListener {
            e.sharePDF()
            dismiss()
        }

        var layoutShareImage = view.findViewById<LinearLayout>(R.id.layoutShareImage)
        layoutShareImage.setOnClickListener {
            e.shareImage()
            dismiss()
        }

        var layoutShareWord = view.findViewById<LinearLayout>(R.id.layoutShareWord)
        layoutShareWord.setOnClickListener {
            e.shareWord()
            dismiss()
        }

        var layoutShareText = view.findViewById<LinearLayout>(R.id.layoutShareText)
        layoutShareText.setOnClickListener {
            e.shareText()
            dismiss()
        }

        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        e.dismiss()
        super.onDismiss(dialog)
    }

    override fun getTheme(): Int {
        return super.getTheme()
    }
}