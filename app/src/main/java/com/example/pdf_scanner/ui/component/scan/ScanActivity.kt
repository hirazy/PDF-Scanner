package com.example.pdf_scanner.ui.component.scan

import android.Manifest
import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.example.pdf_scanner.*
import com.example.pdf_scanner.BuildConfig
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.*
import com.example.pdf_scanner.databinding.ActivityScanBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.detail.DetailActivity
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.ui.component.filter.FilterActivity
import com.example.pdf_scanner.ui.component.history.HistoryActivity
import com.example.pdf_scanner.ui.component.image.ImageActivity
import com.example.pdf_scanner.ui.component.ocr.OCRActivity
import com.example.pdf_scanner.ui.component.scan.adapter.LanguageSelectedAdapter
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScan
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScanEvent
import com.example.pdf_scanner.ui.component.scan.dialog.ShapeBSFragment
import com.example.pdf_scanner.ui.component.scan.dialog.TextEditorDialogFragment
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.PDFDocumentAdapter
import com.example.pdf_scanner.utils.toObject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.oneadx.vpnclient.utils.observe
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author hirazy2001
 * Scan Activity to scan image
 */

@AndroidEntryPoint
class ScanActivity : BaseActivity(), ShapeBSFragment.Properties {

    lateinit var binding: ActivityScanBinding
    val viewModel: ScanViewModel by viewModels()
    lateinit var adapterImg: ImagePageAdapter
    lateinit var adapterLanguageSelected: LanguageSelectedAdapter
    lateinit var listImg: ArrayList<String>
    lateinit var dataScan: DataScan
    lateinit var handler: Handler
    var optionSelected = 0
    lateinit var onEdit: onEditPhoto

    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_app)
        }

        binding = ActivityScanBinding.inflate(layoutInflater)
        dataScan = intent.getStringExtra(KEY_DATA_SCAN)!!.toObject() as DataScan

        if (dataScan.status == KEY_OCR) {
            binding.layoutScanLanguage.visibility = View.VISIBLE
            binding.tbScan.title = OCR
            binding.layoutFilter.visibility = View.GONE

            adapterLanguageSelected = LanguageSelectedAdapter(object : RecyclerItemListener {
                override fun onItemSelected(index: Int, data: OBase) {
                    var intent = Intent(this@ScanActivity, OCRActivity::class.java)
                    startActivityForResult(intent, KEY_RESULT_OCR)
                }

                override fun onOption(index: Int, data: OBase) {

                }
            })

            binding.rcclvScanLanguage.layoutManager =
                LinearLayoutManager(this@ScanActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.rcclvScanLanguage.adapter = adapterLanguageSelected

            viewModel.fetchLanguage()
            binding.layoutScanLanguage.setOnClickListener {
                onScanLanguage()
            }
        }

        listImg = dataScan.listImg

        // Current page 1
        binding.tvCountPage.text = "1 / " + listImg.size.toString()

        setSupportActionBar(binding.tbScan)
        binding.tbScan.setNavigationIcon(R.drawable.ic_back)
        binding.tbScan.setNavigationOnClickListener {
            backToMain()
        }

        binding.layoutAddImage.setOnClickListener {
            addImage()
        }

        binding.animScan.setAnimation(R.raw.scanner)
        binding.animScan.playAnimation()
        binding.animScan.repeatCount = 1

        handler = Handler()
        handler.postDelayed({
            binding.animScan.cancelAnimation()
            binding.animScan.visibility = View.GONE
        }, 4000)

        binding.layoutRotateImage.setOnClickListener {
            rotateImage()
        }

        binding.layoutCropImage.setOnClickListener {
            cropImg()
        }

        binding.layoutSignImage.setOnClickListener {
            signImage()
        }

        binding.layoutText.setOnClickListener {
            addText()
        }

        binding.layoutFilter.setOnClickListener {
            addFilter()
        }

        binding.layoutEraser.setOnClickListener {
            binding.tbScan.title = ERASER
            adapterImg.listFrg[binding.vpgImg.currentItem].erase()
        }

        binding.layoutDeleteImage.setOnClickListener {
            deleteImage()
        }

        binding.btnPageBack.setOnClickListener {
            pageBack()
        }

        binding.btnPageNext.setOnClickListener {
            pageNext()
        }

        binding.vpgImg.offscreenPageLimit = listImg.size

        binding.vpgImg.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                var page = position + 1
                binding.tvCountPage.text = page.toString() + " / " + listImg.size
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        adapterImg = ImagePageAdapter(supportFragmentManager, listImg)
        binding.vpgImg.adapter = adapterImg
        setContentView(binding.root)
    }

    private fun backToMain() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_leave_scan)

        var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        var btnLeave = dialog.findViewById<Button>(R.id.btnAcceptLeave)
        btnLeave.setOnClickListener {
            finish()
        }
        dialog.show()
    }

    private fun onScanLanguage() {
        var intent = Intent(this@ScanActivity, OCRActivity::class.java)
        startActivityForResult(intent, KEY_RESULT_OCR)
    }

    private fun addImage() {
        var bottomDialog = BottomScan(object : BottomScanEvent {
            override fun takePhoto() {
                finish()
            }

            override fun selectAlbum() {
                var intent = Intent(this@ScanActivity, ImageActivity::class.java)
                intent.putExtra(KEY_INTENT_IMAGE, DataImage(dataScan.status).toJSON())
                startActivity(intent)
            }
        })
        bottomDialog.show(supportFragmentManager, bottomDialog.tag)
    }

    private fun cropImg() {
        var ind = binding.vpgImg.currentItem

        var uriFile = Uri.fromFile(File(listImg[ind]))

        adapterImg.cropImage({
            UCrop.of(uriFile, uriFile)
                .withAspectRatio(RESOLUTION_WIDTH_FLOAT, RESOLUTION_HEIGHT_FLOAT)
                .withMaxResultSize(RESOLUTION_WIDTH, RESOLUTION_HEIGHT)
                .start(this)
        }, ind)
    }

    private fun rotateImage() {
        adapterImg.listFrg[binding.vpgImg.currentItem].rotate()
    }

    private fun signImage() {
        var dialog = ShapeBSFragment()
        showBottomSheetDialogFragment(dialog)
        binding.tbScan.title = SIGN
        adapterImg.listFrg[binding.vpgImg.currentItem].sign()
        dialog.setPropertiesChangeListener(this)
    }

    private fun addText() {
        var dialogText: TextEditorDialogFragment = TextEditorDialogFragment.show(
            this
        )

        dialogText.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
            override fun onDone(inputText: String?, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                styleBuilder.withTextSize(viewModel.textSizeLive.value!!.data!!.toFloat())
                adapterImg.listFrg[binding.vpgImg.currentItem].addTextO(inputText!!, styleBuilder)
            }
        })
    }

    private fun addFilter() {
        var intent = Intent(this@ScanActivity, FilterActivity::class.java)
        /**
         * listImg.size > 1 to Filter All
         */
        intent.putExtra(
            KEY_DATA_FILTER,
            DataFilter(listImg.size > 1, listImg[binding.vpgImg.currentItem]).toJSON()
        )
        startActivityForResult(intent, KEY_RESULT_FILTER)
    }

    private fun deleteImage() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_leave_scan)

        var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        var btnLeave = dialog.findViewById<Button>(R.id.btnAcceptLeave)
        btnLeave.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun pageBack() {
        var pos = binding.vpgImg.currentItem
        if (pos >= 1) {
            binding.vpgImg.setCurrentItem(pos - 1, true)
            var page = pos
            binding.tvCountPage.text = page.toString() + " / " + listImg.size
        }
    }

    private fun pageNext() {
        var pos = binding.vpgImg.currentItem + 1
        if (pos < adapterImg.count) {
            binding.vpgImg.setCurrentItem(pos, true)
            var page = pos + 1
            binding.tvCountPage.text = page.toString() + " / " + listImg.size
        }
    }

    private fun deleteFilePath(path: String) {
        val file: File = File(path)
        file.delete()
        if (file.exists()) {
            file.canonicalFile.delete()
            if (file.exists()) {
                applicationContext.deleteFile(file.name)
            }
        }
    }

    interface onEditPhoto {

        fun addText(inputText: String, styleBuilder: TextStyleBuilder)

        fun savePhoto()
    }

    @JvmName("setOnEdit1")
    fun setOnEdit(o: onEditPhoto) {
        onEdit = o
    }

    override fun observeViewModel() {
        observe(viewModel.listLanguage, ::handleLanguageOCR)
    }

    private fun handleLanguageOCR(list: Resource<ArrayList<String>>) {
        when (list) {
            is Resource.Success -> {
                var it = list.data
                var listLanguage = ArrayList<DataLanguage>()
                for (i in 0 until it!!.size)
                    listLanguage.add(DataLanguage(it[i]))
                adapterLanguageSelected.setData(listLanguage)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (dataScan.status == KEY_OCR) {
            menuInflater.inflate(R.menu.item_action_ocr, menu)
        } else {
            menuInflater.inflate(R.menu.item_action_scan, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionSave -> {
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_save)
                var animSave = dialog.findViewById<LottieAnimationView>(R.id.animSave)
                animSave.setAnimation(R.raw.save_scan)
                animSave.repeatCount = 1
                animSave.playAnimation()

                var folderNameTv = dialog.findViewById<TextView>(R.id.tvFolderName)

                val fileRoot = FileUtil(this@ScanActivity).getRootFolder()
                var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var pathNameSaved = "/saved/"
                var folderName = "Scan " + date.format(Date())
                val strTime = "$folderName/"
                var folderSavedPath = fileRoot + pathNameSaved + strTime
                var folderSaved = File(folderSavedPath)
                folderSaved.mkdirs()

                folderNameTv.text = folderName

                var txtTime = dialog.findViewById<TextView>(R.id.tvSaveTime)
                txtTime.text = "Today, " + date.format(Date()).substring(5, 10)

                for (i in 0 until listImg.size) {
                    var count = i + 1
                    var name = ""
                    if (count < 10) {
                        name = "0$count"
                    } else {
                        name = count.toString()
                    }
                    adapterImg.listFrg[i].save(strTime, name)
                }

                var layoutOptionSave = dialog.findViewById<RelativeLayout>(R.id.layoutOptionSave)
                var layoutName = dialog.findViewById<LinearLayout>(R.id.layoutSaveName)
                layoutName.setOnClickListener {
                    var dialogName = Dialog(this)
                    dialogName.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialogName.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialogName.setCancelable(false)
                    dialogName.setContentView(R.layout.dialog_rename)

                    var textName = dialogName.findViewById<EditText>(R.id.edtReName)
                    textName.setText(folderName)
                    textName.isFocusableInTouchMode = true

                    var deleteName = dialogName.findViewById<ImageButton>(R.id.btnDeleteName)
                    deleteName.setOnClickListener {
                        textName.setText("")
                        textName.isFocusable = true
                    }

                    var cancelBtn = dialogName.findViewById<Button>(R.id.btnDeclineReName)
                    cancelBtn.setOnClickListener {
                        dialogName.dismiss()
                    }

                    var acceptBtn = dialogName.findViewById<Button>(R.id.btnAcceptRename)
                    acceptBtn.setOnClickListener {
                        if (textName.text.isEmpty()) {
                            DynamicToast.makeWarning(this@ScanActivity, "Empty Name!").show()
                            return@setOnClickListener
                        }
                        var folderRoot = fileRoot + pathNameSaved
                        var folderNameTmp = textName.text.toString()
                        var file = File(folderRoot + folderNameTmp)

                        var plusName = "(1)"
                        while (file.exists()) {
                            file = File(folderRoot + folderNameTmp)
                            folderNameTmp += plusName
                        }
                        var isSuccess = folderSaved.renameTo(file)
                        if (isSuccess) {
                            folderNameTv.text = folderNameTmp
                            DynamicToast.makeSuccess(
                                this@ScanActivity,
                                "Rename Folder Successfully!"
                            ).show()
                        } else {
                            DynamicToast.makeError(
                                this@ScanActivity,
                                "Cannot rename Folder!"
                            ).show()
                        }
                        dialogName.dismiss()
                    }
                    dialogName.show()
                }

                var layoutShare = dialog.findViewById<RelativeLayout>(R.id.layoutSaveShare)
                layoutShare.setOnClickListener {
                    dialog.dismiss()
                    var bottomShare = BottomShare(object : BottomShareEvent {
                        override fun sharePDF() {

                            var filePDF = File(filePath(strTime, PDF))
                            var isSuccess = filePDF.createNewFile()

                            if (!isSuccess) {
                                DynamicToast.makeError(this@ScanActivity, "Create file error!")
                                    .show()
                                return
                            }
                            val pdfWriter = PdfWriter(filePDF)
                            val pdfDocument = PdfDocument(pdfWriter)
                            val document = Document(pdfDocument)

                            for (i in 0 until listImg.size) {
                                var count = i + 1
                                var name = ""
                                if (count < 10) {
                                    name = "0$count"
                                } else {
                                    name = count.toString()
                                }
                                var filePath = "$fileRoot/saved/$folderName/$name.jpg"
                                if (!File(filePath).exists()) {
                                    DynamicToast.makeError(this@ScanActivity, "Create file error!")
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
                                uriFromFile(this@ScanActivity, filePDF)
                            )
                            sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            sharingIntent.type = "application/pdf"
                            startActivity(Intent.createChooser(sharingIntent, "Share PDF"))
                        }

                        override fun shareImage() {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND_MULTIPLE
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
                            intent.type = "image/jpeg"
                            val files = ArrayList<Uri>()
                            var fileRoot = File(folderSavedPath)
                            var listFile = fileRoot.listFiles()
                            if (!fileRoot.exists() || listFile == null) {
                                Toast.makeText(
                                    this@ScanActivity,
                                    "Cannot share image!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                            for (f in listFile) {
                                val file = File(f.path)
                                val uri = Uri.fromFile(file)
                                files.add(uri)
                            }
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
                            startActivity(Intent.createChooser(intent, "Share Image"))
                        }

                        override fun shareWord() {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND_MULTIPLE
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
                            intent.type = "image/jpeg"
                            val files = ArrayList<Uri>()
                            var fileRoot = File(folderSavedPath)
                            var listFile = fileRoot.listFiles()
                            for (f in listFile) {
                                val file = File(f.path)
                                val uri = Uri.fromFile(file)
                                files.add(uri)
                            }
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
                            startActivity(Intent.createChooser(intent, "Share Image"))
                        }

                        override fun shareText() {

                        }

                        override fun dismiss() {
                            finish()
                        }
                    })
                    bottomShare.show(supportFragmentManager, bottomShare.tag)
                }

                var layoutPrint = dialog.findViewById<RelativeLayout>(R.id.layoutSavePrint)
                layoutPrint.setOnClickListener {
                    printFolder(folderName)
                }

                var layoutToAlbum = dialog.findViewById<RelativeLayout>(R.id.layoutSaveToAlbum)
                layoutToAlbum.setOnClickListener {
                    var intent = Intent(this@ScanActivity, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                var layoutEmail = dialog.findViewById<RelativeLayout>(R.id.layoutSaveEmail)
                layoutEmail.setOnClickListener {
                    // var filePDF =
                }

                var btnClose = dialog.findViewById<ImageButton>(R.id.btnCloseSave)
                btnClose.setOnClickListener {
                    dialog.dismiss()
                    var intent = Intent(this@ScanActivity, HistoryActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                var layoutAnimation = dialog.findViewById<LinearLayout>(R.id.layoutAnimationSave)
                animSave.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        layoutAnimation.visibility = View.GONE
                        layoutOptionSave.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }
                })
                dialog.show()
            }

            R.id.actionUndo -> {
                adapterImg.listFrg[binding.vpgImg.currentItem].unDo()
            }

            R.id.actionRedo -> {
                adapterImg.listFrg[binding.vpgImg.currentItem].reDo()
            }

            R.id.actionOCR -> {
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_save)
                var animSave = dialog.findViewById<LottieAnimationView>(R.id.animSave)
                animSave.setAnimation(R.raw.save_scan)
                animSave.repeatCount = 1
                animSave.playAnimation()

                var folderNameTv = dialog.findViewById<TextView>(R.id.tvFolderName)

                val fileRoot = FileUtil(this@ScanActivity).getRootFolder()
                var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var pathNameSaved = "/saved/"
                var folderName = "Scan " + date.format(Date())
                val strTime = "$folderName/"
                var folderSavedPath = fileRoot + pathNameSaved + strTime
                var folderSaved = File(folderSavedPath)
                folderSaved.mkdirs()

                folderNameTv.text = folderName

                var txtTime = dialog.findViewById<TextView>(R.id.tvSaveTime)
                txtTime.text = "Today, " + date.format(Date()).substring(5, 10)
                var listPath = ArrayList<String>()
                var filePath: String = ""

                for (i in 0 until listImg.size) {
                    var count = i + 1
                    var name = ""
                    if (count < 10) {
                        name = "0$count"
                    } else {
                        name = count.toString()
                    }
                    filePath = "$fileRoot/saved/$folderName/$name.jpg"
                    listPath.add(filePath)
                    adapterImg.listFrg[i].save(strTime, name)
                }

                var layoutAnimation = dialog.findViewById<LinearLayout>(R.id.layoutAnimationSave)
                animSave.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        var intent = Intent(this@ScanActivity, DetailActivity::class.java)
                        intent.putExtra(
                            KEY_DATA_DETAIL,
                            ImageFolder(
                                folderName, strTime, listPath
                            ).toJSON()
                        )

                        layoutAnimation.visibility = View.GONE
                        dialog.dismiss()
                        startActivity(intent)
                        finish() // Finish Activity
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }
                })
                dialog.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun filePath(nameFolder: String, typePath: String): String {
        var date1 = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Date())
        var date2 = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var nameFile = "FILE_$date2" + "_Scan $date1"
        var fileRoot = FileUtil(this@ScanActivity).getRootFolder()
        var path = "$fileRoot/saved/$nameFolder$nameFile$typePath"
        return path
    }

    private fun printFolder(nameFolder: String) {
        var filePDF = File(filePath("$nameFolder/", PDF))
        var isSuccess = filePDF.createNewFile()

        if (!isSuccess) {
            DynamicToast.makeError(this@ScanActivity, "Create file error!")
                .show()
            return
        }
        val pdfWriter = PdfWriter(filePDF)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        var list = listImg

        for (i in 0 until list.size) {
            var filePath = list[i]
            if (!File(filePath).exists()) {
                DynamicToast.makeError(this@ScanActivity, "Create file error!")
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

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onColorChanged(colorCode: Int) {
        adapterImg.listFrg[binding.vpgImg.currentItem].setOnCoLorChanged(colorCode)
    }

    override fun onOpacityChanged(opacity: Int) {
        adapterImg.listFrg[binding.vpgImg.currentItem].setOnOpacityChanged(opacity)
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        adapterImg.listFrg[binding.vpgImg.currentItem].setOnShapeSizeChanged(shapeSize)
    }

    override fun onShapePicked(shapeType: ShapeType?) {
        adapterImg.listFrg[binding.vpgImg.currentItem].setShape(shapeType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                KEY_RESULT_OCR -> {
                    if (dataScan.status == KEY_OCR) {
                        /**
                         * Reset OCR language
                         */
                        viewModel.fetchLanguage()
                    }
                }

                KEY_RESULT_FILTER -> {
                    /**
                     * Set Filter for current page
                     */
                    var filter = data!!.getStringExtra(KEY_FILTER)!!.toObject<DataResultFilter>()
                    if (filter.filter != PhotoFilter.NONE) {
                        if (filter.isFilterAll) {
                            for (i in 0 until listImg.size) {
                                adapterImg.listFrg[i].setFilter(filter.filter)
                            }
                        } else {
                            adapterImg.listFrg[binding.vpgImg.currentItem].setFilter(filter.filter)
                        }
                    }
                }

                UCrop.REQUEST_CROP -> {
                    adapterImg.listFrg[binding.vpgImg.currentItem].resetSrc()
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            var cropError = UCrop.getError(data!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        backToMain()
    }

    class ImageFragment(val list: ArrayList<String>) : ListFragment(), OnPhotoEditorListener,
        onEditPhoto {

        lateinit var mPhotoEditor: PhotoEditor
        lateinit var mPhotoEditorView: PhotoEditorView
        lateinit var mShapeBuilder: ShapeBuilder
        var position = 0
        var mNum: Int = 0

        fun newInstance(number: Int): ImageFragment? {
            val args = Bundle()
            args.putInt(NUMBER, number)
            val fragment = ImageFragment(list)
            fragment.arguments = args
            position = number
            return fragment
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            mNum = if (arguments != null) requireArguments().getInt(NUMBER) else 1
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val root: View = inflater.inflate(R.layout.item_page_image, container, false)
            val number = requireArguments().getInt(NUMBER)
            mPhotoEditorView = root!!.findViewById(R.id.photoEditor)

            mPhotoEditor = PhotoEditor.Builder(context, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build()

            var file = File(list[number])

            if (file.exists()) {
                val uri: Uri = Uri.fromFile(file)
                mPhotoEditorView!!.source.setImageURI(uri)
            }

            mPhotoEditor!!.setOnPhotoEditorListener(this)
            mShapeBuilder = ShapeBuilder()
            mPhotoEditor!!.setShape(mShapeBuilder)

            mPhotoEditor!!.setFilterEffect(PhotoFilter.BLACK_WHITE)

            var act = activity as ScanActivity
            act.setOnEdit(object : onEditPhoto {
                override fun addText(inputText: String, styleBuilder: TextStyleBuilder) {
                    if (mNum == requireArguments().getInt(NUMBER)) {
                        mPhotoEditor!!.addText(inputText, styleBuilder)
                    }
                }

                override fun savePhoto() {

                }
            })

            return root
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            listAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_list_item_1, list
            )
        }

        fun resetSrc() {
            val number = requireArguments().getInt(NUMBER)
            var file = File(list[number])

            if (file.exists()) {
                val uri: Uri = Uri.fromFile(file)
                mPhotoEditorView!!.source.setImageURI(uri)
            }
        }

        fun addTextO(inputText: String, styleBuilder: TextStyleBuilder) {
            mPhotoEditor!!.addText(inputText, styleBuilder)
        }

        fun rotate() {
            mPhotoEditor.setFilterEffect(PhotoFilter.ROTATE)
        }

        fun unDo() {
            mPhotoEditor.undo()
        }

        fun reDo() {
            mPhotoEditor.redo()
        }

        fun erase() {
            mPhotoEditor.brushEraser()
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
        }

        fun setFilter(filter: PhotoFilter) {
            mPhotoEditor.setFilterEffect(filter)
        }

        fun save(folderName: String, name: String) {
            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()

            var fileRoot = FileUtil(requireContext()).getRootFolder()
            var filePath = "$fileRoot/saved/$folderName/$name.jpg"

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            mPhotoEditor!!.saveAsFile(filePath, saveSettings, object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {

                }

                override fun onFailure(exception: Exception) {

                }
            })
        }

        fun saveCrop(filePath: String, call: () -> Unit) {
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

            mPhotoEditor!!.saveAsFile(filePath, saveSettings, object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    Log.e("saveAsFile", "1")
                    call.invoke()
                }

                override fun onFailure(exception: Exception) {

                }
            })
        }

        override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {

        }

        override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

        }

        override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

        }

        override fun onStartViewChangeListener(viewType: ViewType?) {

        }

        override fun onStopViewChangeListener(viewType: ViewType?) {

        }

        override fun onTouchSourceImage(event: MotionEvent?) {

        }

        override fun addText(inputText: String, styleBuilder: TextStyleBuilder) {
            mPhotoEditor.addText(inputText, styleBuilder)
        }

        override fun savePhoto() {

        }
    }

    class ImagePageAdapter(fm: FragmentManager, var list: ArrayList<String>) :
        FragmentStatePagerAdapter(fm) {

        var listFrg = ArrayList<ImageFragment>()

        var position = 0
        var mNum: Int = 0

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return super.instantiateItem(container, position)
        }

        override fun getItem(position: Int): Fragment {
            if (listFrg.size > position)
                return listFrg[position]
            var fragment: ImageFragment? = null
            try {
                fragment = ImageFragment(list).newInstance(position)
                listFrg.add(fragment!!)
            } catch (e: Exception) {
            }
            return fragment!!
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

        fun canDelete(): Boolean {
            return list.size > 0
        }

        fun cropImage(call: () -> Unit, position: Int) {
            var frag = listFrg[position]
            frag.saveCrop(list[position]) {
                call.invoke()
                Log.e("saveAsFile", "2")
            }
        }
    }
}