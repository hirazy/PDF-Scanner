package com.example.pdf_scanner.ui.component.history

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.*
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataSearch
import com.example.pdf_scanner.data.dto.DataSelect
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityHistoryBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecycleFolderListener
import com.example.pdf_scanner.ui.component.detail.DetailActivity
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.ui.component.history.adapter.FolderAdapter
import com.example.pdf_scanner.ui.component.history.dialog.BottomMore
import com.example.pdf_scanner.ui.component.history.dialog.BottomMoreEvent
import com.example.pdf_scanner.ui.component.main.MainActivity
import com.example.pdf_scanner.ui.component.search.SearchActivity
import com.example.pdf_scanner.ui.component.select.SelectActivity
import com.example.pdf_scanner.ui.component.settings.SettingsActivity
import com.example.pdf_scanner.utils.FileUtil
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
class HistoryActivity : BaseActivity() {

    val viewModel: HistoryViewModel by viewModels()
    lateinit var binding: ActivityHistoryBinding
    lateinit var adapter: FolderAdapter
    lateinit var listFolder: ArrayList<ImageFolder>
    private var isExitAgain: Boolean = false

    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.colorApp)
        }

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbHistory)

        binding.tbHistory.setNavigationOnClickListener {
            finish()
        }

        binding.fabNewScan.setOnClickListener {
            if (viewModel.liveStartCamera.value!!.data == true) {
                var intent = Intent(this@HistoryActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                finish()
            }
        }

        binding.layoutSearchFolder.setOnClickListener {
            var intent = Intent(this@HistoryActivity, SearchActivity::class.java)
            intent.putExtra(KEY_DATA_SEARCH, DataSearch(listFolder).toJSON())
            startActivityForResult(intent, 1)
        }

        adapter = FolderAdapter(object : RecycleFolderListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var o = data as ImageFolder
                var intent = Intent(this@HistoryActivity, DetailActivity::class.java)
                intent.putExtra(KEY_DATA_DETAIL, o.toJSON())
                startActivityForResult(intent, 1)
            }

            override fun onItemDelete(index: Int, data: OBase) {
                var dialog = Dialog(this@HistoryActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_leave_scan)

                var txtTitle = dialog.findViewById<TextView>(R.id.tvContentLeave)
                txtTitle.setText(R.string.tvDelete)

                var txtContent = dialog.findViewById<TextView>(R.id.tvContentLeaveScan)
                txtContent.setText(R.string.tvContentDelete)

                var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
                btnCancel.setText(R.string.tvCancel)
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }
                var btnDelete = dialog.findViewById<Button>(R.id.btnAcceptLeave)
                btnDelete.setText(R.string.tvDelete)
                btnDelete.setOnClickListener {
                    var o = data as ImageFolder
                    var pathUtil = FileUtil(this@HistoryActivity).getRootFolder()
                    var filePath = pathUtil + "/saved/" + o.name
                    var file = File(filePath)
                    recursiveDelete(file)
                    fetchFolder()
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun onItemRename(index: Int, data: OBase) {
                var o = data as ImageFolder
                var dialog = Dialog(this@HistoryActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_rename)

                var nameFolder = dialog.findViewById<EditText>(R.id.edtReName)
                nameFolder.setText(o.name)

                var btnDelete = dialog.findViewById<ImageButton>(R.id.btnDeleteName)
                btnDelete.setOnClickListener {
                    nameFolder.setText("")
                    nameFolder.isFocusableInTouchMode = true
                    nameFolder.isFocusable = true
                    nameFolder.isEnabled = true
                }

                var btnCancel = dialog.findViewById<Button>(R.id.btnDeclineReName)
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                var btnAccept = dialog.findViewById<Button>(R.id.btnAcceptRename)
                btnAccept.setOnClickListener {
                    var name = nameFolder.text.toString()
                    if (name.isEmpty()) {
                        DynamicToast.makeError(
                            this@HistoryActivity,
                            "Text is empty!"
                        ).show()
                        return@setOnClickListener
                    }
                    var pathUtil = FileUtil(this@HistoryActivity).getRootFolder()
                    var pathNameSaved = "/saved/"
                    var pathRootFolder = pathUtil + pathNameSaved
                    var pathFileSaved = pathUtil + pathNameSaved + o.name
                    val oldFolder = File(pathFileSaved)
                    var newFolder =
                        File(pathRootFolder + name)
                    var plusName = "(1)"
                    while (newFolder.exists()) {
                        name += plusName
                        newFolder = File(pathRootFolder + name)
                    }

                    var isSuccess = oldFolder.renameTo(newFolder)
                    if (isSuccess) {
                        fetchFolder()
                        DynamicToast.makeSuccess(
                            this@HistoryActivity,
                            "Rename folder successfully!"
                        ).show()
                    } else {
                        DynamicToast.makeSuccess(this@HistoryActivity, "Cannot rename folder!")
                            .show()
                    }
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun onItemMore(index: Int, data: OBase) {
                var o = data as ImageFolder

                val fileRoot = FileUtil(this@HistoryActivity).getRootFolder()
                var pathNameSaved = "/saved/"
                val nameFolder = o.name
                var folderSavedPath = fileRoot + pathNameSaved + nameFolder

                var bottomMore = BottomMore(object : BottomMoreEvent {
                    override fun onMove() {

                    }

                    override fun onCopy() {
                        // var folderCopy = File()

                    }

                    override fun onShare() {
                        var bottomShare = BottomShare(object : BottomShareEvent {
                            override fun sharePDF() {
                                sharePDF(o)
                            }

                            override fun shareImage() {
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
                            }
                        })
                        bottomShare.show(supportFragmentManager, bottomShare.tag)
                    }
                })

                bottomMore.show(supportFragmentManager, bottomMore.tag)
            }
        })

        binding.rcclvFolder.layoutManager = LinearLayoutManager(this)
        binding.rcclvFolder.adapter = adapter
        fetchFolder()
    }

    private fun fetchFolder() {
        var list = ArrayList<ImageFolder>()
        var pathSaved = "/saved"
        var filePath = FileUtil(this@HistoryActivity).getRootFolder() + pathSaved
        val directory = File(filePath)
        listFolder = ArrayList()
        if (!directory.exists() || directory.listFiles() == null) {
            return
        }
        val files: Array<File> = directory.listFiles().reversedArray()
        for (i in files.indices) {
            var listPath = ArrayList<String>()
            if (files[i].listFiles() == null) {
                continue
            }
            for (f in files[i].listFiles()) {
                if (f.name.endsWith(JPG)) {
                    listPath.add(f.path)
                }
            }
            if (listPath.size == 0) {
                files[i].delete()
                continue
            }

            var fileTimeCreated = SimpleDateFormat("yyyy/MM/dd").format(
                Date(files[i].lastModified())
            );
            list.add(ImageFolder(files[i].name, fileTimeCreated, listPath))
        }
        listFolder = list
        viewModel.fetchData(list)
    }

    private fun recursiveDelete(file: File) {
        if (!file.exists()) return

        if (file.isDirectory) {
            for (f in file.listFiles()) {
                recursiveDelete(f)
            }
        }
        file.delete()
    }

    private fun sharePDF(o: ImageFolder) {
        var filePDF = File(filePath(o.name, PDF))
        var isSuccess = filePDF.createNewFile()

        if (!isSuccess) {
            DynamicToast.makeError(this@HistoryActivity, "Create file error!")
                .show()
            return
        }
        val pdfWriter = PdfWriter(filePDF)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        var listImg = o.list

        for (i in 0 until listImg.size) {
            if (!File(listImg[i]).exists()) {
                DynamicToast.makeError(this@HistoryActivity, "Create file error!")
                    .show()
                return
            }

            val filePath = listImg[i]

            val imageData = ImageDataFactory.create(filePath)
            val pdfImg = Image(imageData)
            document.add(pdfImg)
        }

        document.close()
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.putExtra(
            Intent.EXTRA_STREAM,
            uriFromFile(this@HistoryActivity, filePDF)
        )
        sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sharingIntent.type = "application/pdf"
        startActivity(Intent.createChooser(sharingIntent, "Share PDF"))
    }

    private fun shareImage(o: ImageFolder) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
        intent.type = "image/jpeg"
        val files = ArrayList<Uri>()
        val fileRoot = FileUtil(this@HistoryActivity).getRootFolder()
        var pathNameSaved = "/saved/"
        var folderSavedPath = fileRoot + pathNameSaved + o.name + "/"

        var folder = File(folderSavedPath)
        var listFile = folder.listFiles()
        if (!folder.exists() || listFile == null) {
            Toast.makeText(
                this@HistoryActivity,
                "Cannot share image!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        for (f in listFile) {
            val file = File(f.path)
            val uri = uriFromFile(this@HistoryActivity, file)
            files.add(uri)
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    private fun shareDoc() {

    }

    private fun shareText() {

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

    private fun filePath(nameFolder: String, typePath: String): String {
        var date1 = SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(Date())
        var date2 = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var nameFile = "FILE_$date2" + "_Scan $date1"
        var fileRoot = FileUtil(this@HistoryActivity).getRootFolder()
        var path = "$fileRoot/saved/$nameFolder$nameFile$typePath"
        Log.e("filePath", path)
        return path
    }

    override fun observeViewModel() {
        observe(viewModel.listLiveData, ::handleFolder)
        observe(viewModel.liveStartCamera, ::handleCamera)
    }

    private fun handleFolder(data: Resource<ArrayList<ImageFolder>>) {
        when (data) {
            is Resource.Success -> {
                listFolder = data.data!!
                adapter.setValue(listFolder)
                adapter.notifyDataSetChanged()
                if (listFolder.size == 0 && binding.layoutFolderEmpty.visibility == View.GONE) {
                    binding.layoutFolderEmpty.visibility = View.VISIBLE
                } else if (listFolder.size != 0 && binding.layoutFolderEmpty.visibility == View.VISIBLE) {
                    binding.layoutFolderEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun handleCamera(data: Resource<Boolean>) {
        when (data) {
            is Resource.Success -> {
                Log.e("handleCamera", data.data.toString())
                if (data.data!!) {
                    binding.tbHistory.setNavigationIcon(R.drawable.ic_back)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemSettings -> {
                var intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.itemDocSelected -> {
                var intent = Intent(this@HistoryActivity, SelectActivity::class.java)
                intent.putExtra(KEY_DATA_SELECT, DataSelect(listFolder).toJSON())
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fetchFolder()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val isStartedCamera = viewModel.liveStartCamera.value!!.data
        if (isStartedCamera == false) {
            if (isExitAgain) {
                super.onBackPressed()
            }

            isExitAgain = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(
                Runnable
                { isExitAgain = false }, 2000
            )
        }
        super.onBackPressed()
    }
}