package com.example.pdf_scanner.ui.component.detail_text.fragment

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pdf_scanner.DIALOG_CONTENT_DELETE
import com.example.pdf_scanner.DIALOG_TITLE_DELETE
import com.example.pdf_scanner.R
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.ui.component.scan.dialog.ShapeBSFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import java.io.File

class ImageDetailFragment(var path: String, var e: OnImageTextListener) : Fragment() {

    lateinit var mPhotoEditor: PhotoEditor
    lateinit var mPhotoEditorView: PhotoEditorView
    lateinit var mShapeBuilder: ShapeBuilder
    var mRotate = PhotoFilter.FLIP_HORIZONTAL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_image_detail, container, false)

        mPhotoEditorView = view!!.findViewById(R.id.imgDetailText)

        mPhotoEditor = PhotoEditor.Builder(context, mPhotoEditorView)
            .setPinchTextScalable(true)
            .build()

        var file = File(path)

        if (file.exists()) {
            val uri: Uri = Uri.fromFile(file)
            mPhotoEditorView!!.source.setImageURI(uri)
            mPhotoEditor!!.setFilterEffect(PhotoFilter.BLACK_WHITE)
        }

        var layoutRotate = view.findViewById<LinearLayout>(R.id.layoutRotateImageText)
        layoutRotate.setOnClickListener {
            rotate()
        }

        var layoutFilter = view.findViewById<LinearLayout>(R.id.layoutFilterImageText)
        layoutFilter.setOnClickListener {
        }

        var layoutSign = view.findViewById<LinearLayout>(R.id.layoutImageSignText)
        layoutSign.setOnClickListener {
            sign()
        }

        var layoutShare = view.findViewById<LinearLayout>(R.id.layoutImageShareText)
        layoutShare.setOnClickListener {
            share()
        }

        var layoutPrint = view.findViewById<LinearLayout>(R.id.layoutImagePrintText)
        layoutPrint.setOnClickListener {

        }

        var layoutSave = view.findViewById<LinearLayout>(R.id.layoutImageAlbumText)
        layoutSave.setOnClickListener {
            Toast.makeText(requireContext(), "Successfully save photo to the album", Toast.LENGTH_SHORT).show()
        }

        var layoutDelete = view.findViewById<LinearLayout>(R.id.layoutImageDeleteText)
        layoutDelete.setOnClickListener {
            var dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_leave_scan)

            var txtDetailTitle = dialog.findViewById<TextView>(R.id.tvContentLeave)
            txtDetailTitle.text = DIALOG_TITLE_DELETE

            var txtDetailContent = dialog.findViewById<TextView>(R.id.tvContentLeaveScan)
            txtDetailContent.text = DIALOG_CONTENT_DELETE

            var btnDetailCancel = dialog.findViewById<Button>(R.id.btnDecline)
            btnDetailCancel.setOnClickListener {
                dialog.dismiss()
            }

            var btnDetailAccept = dialog.findViewById<Button>(R.id.btnAcceptLeave)
            btnDetailAccept.setOnClickListener {
                e.onDelete()
                dialog.dismiss()
            }
            dialog.show()
        }

        return view
    }

    fun rotate() {
        mPhotoEditor.setFilterEffect(PhotoFilter.ROTATE)
    }

    fun share() {
        var bottomShare = BottomShare(object : BottomShareEvent {
            override fun sharePDF() {

                //  var filePDF = File()
            }

            override fun shareImage() {

            }

            override fun shareWord() {

            }

            override fun shareText() {

            }

            override fun dismiss() {

            }
        })
        bottomShare.show(requireFragmentManager(),  bottomShare.tag)
    }

    fun print(){
        e.onPrint()
    }

    fun setShape(shapeType: ShapeType?) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType))
    }

    fun setOnCoLorChanged(colorCode: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode))
    }

    fun setOnOpacityChanged(opacity: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity))
    }

    fun setOnShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize.toFloat()))
    }

    fun sign() {
        mPhotoEditor.setBrushDrawingMode(true)
        mShapeBuilder = ShapeBuilder()
        mPhotoEditor.setShape(mShapeBuilder)
        e.onSign()
    }
}