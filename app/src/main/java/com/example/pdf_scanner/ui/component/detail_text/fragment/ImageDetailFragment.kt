package com.example.pdf_scanner.ui.component.detail_text.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.pdf_scanner.DIALOG_CONTENT_DELETE
import com.example.pdf_scanner.DIALOG_TITLE_DELETE
import com.example.pdf_scanner.PDF
import com.example.pdf_scanner.R
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.PDFDocumentAdapter
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import java.io.File


class ImageDetailFragment(var path: String, var e: OnImageTextListener,
                    ) : Fragment() {

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
            printImageDetail()
        }

        var layoutSave = view.findViewById<LinearLayout>(R.id.layoutImageAlbumText)
        layoutSave.setOnClickListener {
            save()
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

    fun save() {
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(true)
            .setTransparencyEnabled(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mPhotoEditor!!.saveAsFile(path, saveSettings, object : PhotoEditor.OnSaveListener {
            override fun onSuccess(imagePath: String) {
                Toast.makeText(
                    requireContext(),
                    "Successfully save photo to the album!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("onSuccess", "Save OK")
                e.onSave()
            }

            override fun onFailure(exception: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to save photo to the album!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("onFailure", "Save Failed")
                e.onSave()
            }
        })
    }

    fun rotate() {
        mPhotoEditor.setFilterEffect(PhotoFilter.ROTATE)
    }

    fun share() {
        var bottomShare = BottomShare(object : BottomShareEvent {
            override fun sharePDF() {
                sharePDFDetail()
            }

            override fun shareImage() {
                shareImageDetail()
            }

            override fun shareWord() {

            }

            override fun shareText() {

            }

            override fun dismiss() {

            }
        })
        bottomShare.show(requireFragmentManager(), bottomShare.tag)
    }

    private fun sharePDFDetail() {
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(300, 600, 1).create()
        val myPage: PdfDocument.Page = document.startPage(pageInfo)


        // var file = File()
        document.finishPage(myPage)
    }

    private fun shareImageDetail() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
        intent.type = "image/jpeg"
        val files = ArrayList<Uri>()
        val file = File(path)
        val uri = Uri.fromFile(file)
        files.add(uri)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun printImageDetail() {
        var splitList = path.split('/')
        var length = splitList.size
        var fileAllName = splitList[length - 2]

        var filePDF = File(filePath("$fileAllName", PDF))
        var isSuccess = filePDF.createNewFile()

        if (!isSuccess) {
            DynamicToast.makeError(requireContext(), "Create file error!")
                .show()
            return
        }
        val pdfWriter = PdfWriter(filePDF)
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val imageData = ImageDataFactory.create(path)
        val pdfImg = Image(imageData)
        document.add(pdfImg)

        document.close()

        var printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PDFDocumentAdapter(filePDF)
        try {
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        } catch (e: Exception) {
            Log.e("printDetail", e.message.toString())
        }
    }

    private fun shareWordDetail() {

    }

    private fun shareTextDetail() {

    }

    fun print() {
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

    private fun filePath(nameFolder: String, typePath: String): String {
        var nameFile = "FILE_$nameFolder"
        var fileRoot = FileUtil(requireContext()).getRootFolder()
        var path = "$fileRoot/saved/$nameFolder/$nameFile$typePath"
        Log.e("filePath", path)
        return path
    }
}