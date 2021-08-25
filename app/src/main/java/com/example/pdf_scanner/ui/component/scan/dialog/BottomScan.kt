package com.example.pdf_scanner.ui.component.scan.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.pdf_scanner.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomScan(val e: BottomScanEvent): BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.dialog_bottom_add, container, false)
        var layoutTakePhoto = view.findViewById<LinearLayout>(R.id.layoutTakePhoto)
        layoutTakePhoto.setOnClickListener {
            e.takePhoto()
            dismiss()
        }

        var layoutSelectAlbum = view.findViewById<LinearLayout>(R.id.layoutSelectAlbum)
        layoutSelectAlbum.setOnClickListener {
            e.selectAlbum()
            dismiss()
        }
        return view
    }

    override fun getTheme(): Int {
        return super.getTheme()
    }
}