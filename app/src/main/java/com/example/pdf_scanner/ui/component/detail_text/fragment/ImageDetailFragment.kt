package com.example.pdf_scanner.ui.component.detail_text.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.pdf_scanner.R
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

class ImageDetailFragment() : Fragment() {

    lateinit var mPhotoEditor: PhotoEditor
    lateinit var mPhotoEditorView: PhotoEditorView
    lateinit var mShapeBuilder: ShapeBuilder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_image_detail, container, false)

        var layoutRotate = view.findViewById<LinearLayout>(R.id.layoutRotateImageText)
        layoutRotate.setOnClickListener{

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}