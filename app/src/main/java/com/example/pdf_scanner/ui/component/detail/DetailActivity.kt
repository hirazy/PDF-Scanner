package com.example.pdf_scanner.ui.component.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pdf_scanner.*
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataDetailText
import com.example.pdf_scanner.data.dto.ImageDetail
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityDetailBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.detail.adapter.CardImageDetail
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.ui.component.detail_text.DetailTextActivity
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.PDFDocumentAdapter
import com.example.pdf_scanner.utils.toObject
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.oneadx.vpnclient.utils.observe
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DetailActivity : BaseActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var dataImage: ImageFolder
    lateinit var adapter: CardImageDetail
    val viewModel: DetailViewModel by viewModels()

    override fun initViewBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setSupportActionBar(binding.tbDetail)

        dataImage = intent.getStringExtra(KEY_DATA_DETAIL)!!.toObject()

        binding.tvListImage.text = dataImage.name

        binding.layoutCommentDetail.setOnClickListener {

        }

        binding.layoutEmailDetail.setOnClickListener {

        }

        binding.layoutShareDetail.setOnClickListener {
            shareAction()
        }

        binding.layoutSaveDetail.setOnClickListener {
            DynamicToast.makeSuccess(
                this@DetailActivity,
                "Successfully save photo to the album"
            ).show()
        }

        binding.layoutPrintDetail.setOnClickListener {
            printDetail()
        }

        binding.layoutTextDetail.setOnClickListener {
            textDetail()
        }

        binding.btnAddImage.setOnClickListener {
            // var intent = Intent(this@DetailActivity, )
        }

        adapter = CardImageDetail(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var o = data as ImageDetail
                var intent = Intent(this@DetailActivity, DetailTextActivity::class.java)
                intent.putExtra(KEY_DATA_DETAIL_TEXT, DataDetailText(data.path).toJSON())
                startActivity(intent)
            }

            override fun onOption(index: Int, data: OBase) {
            }
        })

        binding.rcclvDetail.adapter = adapter
        binding.rcclvDetail.layoutManager = GridLayoutManager(this, 2)
        viewModel.fetchData(dataImage.list)
        setContentView(binding.root)
    }

    private fun filePath(nameFolder: String, typePath: String): String {
        var date1 = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Date())
        var date2 = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var nameFile = "FILE_$date2" + "_Scan $date1"
        var fileRoot = FileUtil(this@DetailActivity).getRootFolder()
        var path = "$fileRoot/saved/$nameFolder/$nameFile$typePath"
        Log.e("filePath", path)
        return path
    }

    private fun shareFilePDF() {

        val strTime = "${dataImage.name}/"

        var filePDF = File(filePath(strTime, PDF))
        var isSuccess = filePDF.createNewFile()

        if (!isSuccess) {
            DynamicToast.makeError(this@DetailActivity, "Create file error!")
                .show()
            return
        }
        val pdfWriter = PdfWriter(filePDF)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val list = viewModel.listImage.value!!.data!!

        for (i in 0 until list.size) {
            var filePath = list[i].path
            if (!File(filePath).exists()) {
                DynamicToast.makeError(this@DetailActivity, "Create file error!")
                    .show()
                return
            }

            val imageData = ImageDataFactory.create(filePath)
            val pdfImg = Image(imageData)
            document.add(pdfImg)
        }

        document.close()
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.putExtra(
            Intent.EXTRA_STREAM,
            uriFromFile(this@DetailActivity, filePDF)
        )
        sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sharingIntent.type = "application/pdf"
        startActivity(Intent.createChooser(sharingIntent, "Share PDF"))
    }

    private fun shareImageDetail() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
        intent.type = "image/jpeg"
        val files = ArrayList<Uri>()
        var pathUtil = FileUtil(this@DetailActivity).getRootFolder()
        var nameFolder = dataImage.name
        var folderSavedPath = "$pathUtil/saved/$nameFolder"
        var fileRoot = File(folderSavedPath)
        var listFile = fileRoot.listFiles()
        for (f in listFile) {
            val file = File(f.path)
            val uri = uriFromFile(this@DetailActivity, file)
            files.add(uri)
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun shareText() {

    }

    private fun shareWord() {

    }

    private fun shareAction() {
        var bottomShare = BottomShare(object : BottomShareEvent {
            override fun sharePDF() {
                shareFilePDF()
            }

            override fun shareImage() {
                shareImageDetail()
            }

            override fun shareWord() {
                shareWord()
            }

            override fun shareText() {

            }

            override fun dismiss() {

            }
        })
        bottomShare.show(supportFragmentManager, bottomShare.tag)
    }

    fun uriFromFile(context: Context, file: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        } else {
            return Uri.fromFile(file)
        }
    }

    private fun printDetail() {

        var filePDF = File(filePath(dataImage.name + "/", PDF))
        var isSuccess = filePDF.createNewFile()

        if (!isSuccess) {
            DynamicToast.makeError(this@DetailActivity, "Create file error!")
                .show()
            return
        }
        val pdfWriter = PdfWriter(filePDF)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        var list = dataImage.list

        for (i in 0 until list.size) {
            var filePath = list[i]
            if (!File(filePath).exists()) {
                DynamicToast.makeError(this@DetailActivity, "Create file error!")
                    .show()
                return
            }

            val imageData = ImageDataFactory.create(filePath)
            val pdfImg = Image(imageData)
            document.add(pdfImg)
        }

        document.close()

        var printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = PDFDocumentAdapter(filePDF)
        try {
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        } catch (e: Exception) {
            Log.e("printDetail", e.message.toString())
        }
    }

    private fun textDetail() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_rename)

        var edtFileName = dialog.findViewById<EditText>(R.id.edtReName)
        edtFileName.setText(dataImage.name)
        edtFileName.hint = dataImage.name

        var deleteBtn = dialog.findViewById<ImageButton>(R.id.btnDeleteName)
        deleteBtn.setOnClickListener {
            edtFileName.setText("")
        }

        var cancelBtn = dialog.findViewById<Button>(R.id.btnDeclineReName)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        // tvListImage

        var acceptBtn = dialog.findViewById<Button>(R.id.btnAcceptRename)
        acceptBtn.setOnClickListener {
            if (!validateFileName(edtFileName.text.toString())) {
                DynamicToast.makeWarning(this@DetailActivity, "Failed Name!").show()
                return@setOnClickListener
            }

            var nameFolder = dataImage.name
            val fileRoot = FileUtil(this@DetailActivity).getRootFolder()
            val pathNameSaved = "/saved/"
            var folderRoot = fileRoot + pathNameSaved
            var folderNameTmp = edtFileName.text.toString()

            if (nameFolder == folderNameTmp) {
                dialog.dismiss()
                return@setOnClickListener
            }

            var folderSavedPath = "$fileRoot/saved/$nameFolder"
            var fileSaved = File(folderSavedPath)

            var file = File(folderRoot + folderNameTmp)

            var cnt = 1
            var folderNameChange = folderNameTmp

            while (file.exists()) {
                var plusName = "($cnt)"
                folderNameChange = folderNameTmp + plusName
                file = File(folderRoot + folderNameChange)
                cnt++
            }

            var isSuccess = fileSaved.renameTo(file)
            if (isSuccess) {
                binding.tvListImage.text = folderNameTmp
                DynamicToast.makeSuccess(
                    this@DetailActivity,
                    "Rename Folder Successfully!"
                ).show()
            } else {
                DynamicToast.makeError(
                    this@DetailActivity,
                    "Cannot rename Folder!"
                ).show()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun observeViewModel() {
        observe(viewModel.listImage, ::handleImage)
    }

    private fun handleImage(data: Resource<ArrayList<ImageDetail>>) {
        when (data) {
            is Resource.Success -> {
                adapter.setData(data.data!!)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemListSelected -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun validateFileName(name: String): Boolean {
        if (name.isEmpty()) {
            return false
        }
        for (i in name.indices) {
            if (name[i] == '/') {
                return false
            }
        }
        return true
    }

    private fun reFetchData() {
        var listFile = dataImage.list
        var listData = ArrayList<String>()
        for (i in 0 until listFile.size) {
            var file = File(listFile[i])
            if (file.exists()) {
                listData.add(listFile[i])
            }
        }
        dataImage.list = listData
        if (listData.size == 0) {
            finish()
        } else {
            viewModel.fetchData(dataImage.list)
        }
    }

    override fun onRestart() {
        reFetchData()
        super.onRestart()
    }

    override fun onResume() {
        reFetchData()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            reFetchData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}