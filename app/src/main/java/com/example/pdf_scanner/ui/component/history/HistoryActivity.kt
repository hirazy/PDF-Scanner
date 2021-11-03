package com.example.pdf_scanner.ui.component.history

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import com.example.pdf_scanner.data.dto.*
import com.example.pdf_scanner.databinding.ActivityHistoryBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecycleFolderListener
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.detail.DetailActivity
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShare
import com.example.pdf_scanner.ui.component.detail.dialog.BottomShareEvent
import com.example.pdf_scanner.ui.component.history.adapter.FolderAdapter
import com.example.pdf_scanner.ui.component.history.adapter.FolderSelectAdapter
import com.example.pdf_scanner.ui.component.history.dialog.BottomMore
import com.example.pdf_scanner.ui.component.history.dialog.BottomMoreEvent
import com.example.pdf_scanner.ui.component.image.ImageActivity
import com.example.pdf_scanner.ui.component.main.MainActivity
import com.example.pdf_scanner.ui.component.search.SearchActivity
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
    lateinit var adapterSelect: FolderSelectAdapter
    lateinit var listFolder: ArrayList<ImageFolder>
    private var isExitAgain: Boolean = false
    lateinit var listFolderSelect: ArrayList<FolderSelect>
    lateinit var dataFolder: ArrayList<ImageFolder>
    var menuItem: Menu? = null

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
            onNewScan()
        }

        binding.layoutSearchFolder.setOnClickListener {
            onSearchFolder()
        }

        binding.fabNewFolder.setOnClickListener {
            var intent = Intent(this@HistoryActivity, ImageActivity::class.java)
            intent.putExtra(KEY_INTENT_IMAGE, DataImage(status = KEY_FOLDER).toJSON())
            startActivity(intent)
            if (viewModel.liveStartCamera.value!!.data == true) {
                finish()
            }
        }

        binding.btnCancelCopyHistory.setOnClickListener {
            statusFolderCopy()
        }

        binding.btnConfirmCopyHistory.setOnClickListener {
            onCopyFolders(dataFolder)
        }

        binding.btnCancelSelectHistory.setOnClickListener {
            statusFolderSelect()
        }

        binding.btnConfirmSelectHistory.setOnClickListener {
            onSelectAll()
        }

        binding.layoutSelectMove.setOnClickListener {
            statusFolderCopy()
        }

        binding.layoutSelectDelete.setOnClickListener {
            onDeleteSelect()
        }

        binding.layoutSelectCopy.setOnClickListener {
            onSelectCopy()
        }

        binding.layoutSelectEmail.setOnClickListener {
            onEmailSelect()
        }

        binding.animFolderEmpty.setAnimation(R.raw.empty_box)
        binding.animFolderEmpty.playAnimation()

        initFolderSelect()

        initFolder()
        fetchFolder()
    }

    private fun initFolder() {
        adapter = FolderAdapter(object : RecycleFolderListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var o = data as ImageFolder
                var intent = Intent(this@HistoryActivity, DetailActivity::class.java)
                intent.putExtra(KEY_DATA_DETAIL, o.toJSON())
                startActivityForResult(intent, 1)
            }

            override fun onItemDelete(index: Int, data: OBase) {
                var o = data as ImageFolder
                onDeleteItem(o)
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
                    override fun onCopy() {
                        statusCopyMove()
                        dataFolder = ArrayList()
                        dataFolder.add(o)
                    }

                    override fun onShare() {
                        var bottomShare = BottomShare(object : BottomShareEvent {
                            override fun sharePDF() {
                                sharePDF(o)
                            }

                            override fun shareImage() {
                                shareImage(o)
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
    }

    private fun initFolderSelect() {
        adapterSelect = FolderSelectAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var isSelected = listFolderSelect[index].isSelected
                listFolderSelect[index].isSelected = !isSelected
                adapterSelect.notifyItemChanged(index)
                var cnt = 0
                for (i in 0 until listFolderSelect.size) {
                    if (listFolderSelect[i].isSelected) {
                        cnt++
                    }
                }

                if (cnt > 0) {
                    isActiveSelect()
                } else {
                    noItemSelect()
                }
            }

            override fun onOption(index: Int, data: OBase) {

            }
        })

        binding.rcvSelectHistory.layoutManager = LinearLayoutManager(this)
        binding.rcvSelectHistory.adapter = adapterSelect
    }

    private fun onSelectCopy() {
        statusCopyMove()

        dataFolder = ArrayList()

        for (i in 0 until listFolderSelect.size) {
            if (listFolderSelect[i].isSelected) {
                dataFolder.add(listFolderSelect[i].folder)
            }
        }
    }

    private fun onDeleteSelect() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_leave_scan)

        var txtContent = dialog.findViewById<TextView>(R.id.tvContentLeaveScan)
        txtContent.setText(R.string.tvDeleteSelect)

        var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        var btnAccept = dialog.findViewById<Button>(R.id.btnAcceptLeave)
        btnAccept.setOnClickListener {
            for (i in 0 until listFolderSelect.size) {
                if (listFolderSelect[i].isSelected) {
                    deletePathFolders(listFolderSelect[i].folder)
                }
            }
            fetchFolder()
            dialog.dismiss()
            statusFolderCopy()
        }

        dialog.show()
    }

    private fun deletePathFolders(o: ImageFolder) {
        var pathUtil = FileUtil(this@HistoryActivity).getRootFolder()
        var filePath = pathUtil + "/saved/" + o.name
        var file = File(filePath)
        recursiveDelete(file)
    }

    private fun onEmailSelect() {
        var date = SimpleDateFormat("yyyyMMdd_HHmmss")

        val fileRoot = FileUtil(this@HistoryActivity).getRootFolder()
        val pathNameSaved = "/saved/"
        var folderRoot = fileRoot + pathNameSaved
        var filePath = folderRoot + "FILE_" + date.format(Date())

        var fileShare = File(filePath)

        for(i in 0 until listFolderSelect.size){
            var pathFolder = folderRoot + listFolderSelect[i].folder.name
            fileShare.writeText(pathFolder)
        }

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.putExtra(
            Intent.EXTRA_STREAM,
            uriFromFile(this@HistoryActivity, fileShare)
        )
        sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(sharingIntent, "Email"))
    }

    private fun onSelectAll() {
        for (i in 0 until listFolderSelect.size) {
            if (!listFolderSelect[i].isSelected) {
                listFolderSelect[i].isSelected = true
                adapterSelect.notifyItemChanged(i)
            }
        }
        isActiveSelect()
    }

    private fun onCopyFolders(list: ArrayList<ImageFolder>) {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_copy)

        var txtContent = dialog.findViewById<TextView>(R.id.tvContentCopy)
        txtContent.text = list[0].name + TIPS_CONTENT

        var btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmCopy)
        btnConfirm.setOnClickListener {
            binding.layoutHistorySelect.visibility = View.GONE
            for (i in 0 until list.size) {
                copyFolder(list[i])
            }
            dialog.dismiss()
            statusFolderCopy()
            fetchFolder()
        }

        var btnReplace = dialog.findViewById<Button>(R.id.btnReplaceCopy)
        btnReplace.setOnClickListener {
            dialog.dismiss()
        }

        var btnCancel = dialog.findViewById<Button>(R.id.btnCancelCopy)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun copyFolder(o: ImageFolder) {
        var nameFolder = o.name
        val fileRoot = FileUtil(this@HistoryActivity).getRootFolder()
        val pathNameSaved = "/saved/"
        var folderRoot = fileRoot + pathNameSaved
        var pathFolderCur = folderRoot + nameFolder

        var cnt = 1
        var plusName = "($cnt)"
        var folderCopy = File("$pathFolderCur$plusName/")

        while (folderCopy.exists()) {
            cnt++
            plusName = "($cnt)"
            folderCopy = File("$pathFolderCur$plusName/")
        }

        folderCopy.mkdirs()

        for (i in 0 until o.list.size) {
            var cnt = i + 1
            var fileCur = File(o.list[i])
            var fileCopy = File("$pathFolderCur$plusName/$cnt$JPG")
            fileCur.copyTo(fileCopy)
        }
    }

    private fun noItemSelect() {
        binding.layoutSelectMove.isClickable = false
        binding.layoutSelectCopy.isClickable = false
        binding.layoutSelectEmail.isClickable = false
        binding.layoutSelectDelete.isClickable = false

        binding.icSelectMove.setImageResource(R.drawable.ic_select_no_move)
        binding.icSelectCopy.setImageResource(R.drawable.ic_select_no_copy)
        binding.icSelectEmail.setImageResource(R.drawable.ic_select_no_email)
        binding.icSelectDelete.setImageResource(R.drawable.ic_select_no_delete)

        binding.tvSelectMove.setTextColor(resources.getColor(R.color.colorTextUnSelected))
        binding.tvSelectCopy.setTextColor(resources.getColor(R.color.colorTextUnSelected))
        binding.tvSelectEmail.setTextColor(resources.getColor(R.color.colorTextUnSelected))
        binding.tvSelectDelete.setTextColor(resources.getColor(R.color.colorTextUnSelected))
    }

    private fun isActiveSelect() {
        binding.layoutSelectMove.isClickable = true
        binding.layoutSelectCopy.isClickable = true
        binding.layoutSelectEmail.isClickable = true
        binding.layoutSelectDelete.isClickable = true

        binding.icSelectMove.setImageResource(R.drawable.ic_select_move)
        binding.icSelectCopy.setImageResource(R.drawable.ic_select_copy)
        binding.icSelectEmail.setImageResource(R.drawable.ic_select_email)
        binding.icSelectDelete.setImageResource(R.drawable.ic_select_delete)

        binding.tvSelectMove.setTextColor(resources.getColor(R.color.colorTextSelected))
        binding.tvSelectCopy.setTextColor(resources.getColor(R.color.colorTextSelected))
        binding.tvSelectEmail.setTextColor(resources.getColor(R.color.colorTextSelected))
        binding.tvSelectDelete.setTextColor(resources.getColor(R.color.colorTextSelected))
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

        if (menuItem != null) {
            (listFolder.size != 0).also { menuItem!!.findItem(R.id.itemSelectAll).isVisible = it }
        }

        viewModel.fetchData(list)
    }

    private fun onSearchFolder() {
        var intent = Intent(this@HistoryActivity, SearchActivity::class.java)
        intent.putExtra(KEY_DATA_SEARCH, DataSearch(listFolder).toJSON())
        startActivityForResult(intent, 1)
    }

    private fun onNewScan() {
        if (viewModel.liveStartCamera.value!!.data == false) {
            var intent = Intent(this@HistoryActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            finish()
        }
    }

    private fun onDeleteItem(data: ImageFolder) {
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
            var o = data
            var pathUtil = FileUtil(this@HistoryActivity).getRootFolder()
            var filePath = pathUtil + "/saved/" + o.name
            var file = File(filePath)
            recursiveDelete(file)
            fetchFolder()
            dialog.dismiss()
        }
        dialog.show()
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
        var folderSavedPath = fileRoot + pathNameSaved + o.name

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

    private fun uriFromFile(context: Context, file: File): Uri {
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
        return path
    }

    private fun statusFolderCopy() {
        binding.layoutHistoryFolder.visibility = View.VISIBLE
        binding.layoutHistoryCopy.visibility = View.GONE
        binding.layoutHistorySelect.visibility = View.GONE
    }

    private fun statusCopyMove() {
        binding.layoutHistoryFolder.visibility = View.GONE
        binding.layoutHistoryCopy.visibility = View.VISIBLE
        binding.layoutHistorySelect.visibility = View.GONE
    }

    private fun statusSelect() {
        binding.layoutHistoryFolder.visibility = View.GONE
        binding.layoutHistoryCopy.visibility = View.GONE
        binding.layoutHistorySelect.visibility = View.VISIBLE

        listFolderSelect = ArrayList()
        for (i in 0 until listFolder.size) {
            listFolderSelect.add(FolderSelect(folder = listFolder[i]))
        }

        adapterSelect.setData(listFolderSelect)
    }

    private fun statusFolderSelect() {
        binding.layoutHistoryFolder.visibility = View.VISIBLE
        binding.layoutHistoryCopy.visibility = View.GONE
        binding.layoutHistorySelect.visibility = View.GONE

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
                if (data.data!!) {
                    binding.tbHistory.setNavigationIcon(R.drawable.ic_back)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_history, menu)
        menuItem = menu!!
        (listFolder.size != 0).also { menuItem!!.findItem(R.id.itemSelectAll).isVisible = it }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemSelectAll -> {
                noItemSelect()
                statusSelect()
            }

            R.id.itemSettings -> {
                var intent = Intent(this, SettingsActivity::class.java)
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
                finishAffinity()
            }

            isExitAgain = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(
                Runnable
                { isExitAgain = false }, 2000
            )
        } else {
            super.onBackPressed()
        }
    }
}