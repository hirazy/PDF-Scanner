package com.example.pdf_scanner.ui.component.history.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.pdf_scanner.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomMore(val e: BottomMoreEvent) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.dialog_bottom_more, container, false)
        var layoutMove = view.findViewById<LinearLayout>(R.id.layoutMove)
        layoutMove.setOnClickListener {
            e.onMove()
            dismiss()
        }

        var layoutCopy = view.findViewById<LinearLayout>(R.id.layoutCopy)
        layoutCopy.setOnClickListener {
            e.onCopy()
            dismiss()
        }

        var layoutShare = view.findViewById<LinearLayout>(R.id.layoutShare)
        layoutShare.setOnClickListener {
            e.onShare()
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int {
        return super.getTheme()
    }
}