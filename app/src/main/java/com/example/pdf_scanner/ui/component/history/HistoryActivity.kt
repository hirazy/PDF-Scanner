package com.example.pdf_scanner.ui.component.history

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
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
import com.example.pdf_scanner.ui.component.history.adapter.FolderAdapter
import com.example.pdf_scanner.ui.component.history.dialog.BottomMore
import com.example.pdf_scanner.ui.component.history.dialog.BottomMoreEvent
import com.example.pdf_scanner.ui.component.main.MainActivity
import com.example.pdf_scanner.ui.component.search.SearchActivity
import com.example.pdf_scanner.ui.component.select.SelectActivity
import com.example.pdf_scanner.ui.component.settings.SettingsActivity
import com.example.pdf_scanner.utils.FileUtil
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
            startActivity(intent)
        }

        adapter = FolderAdapter(object : RecycleFolderListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var o = data as ImageFolder
                var intent = Intent(this@HistoryActivity, DetailActivity::class.java)
                intent.putExtra(KEY_DATA_DETAIL, o.toJSON())
                startActivity(intent)
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
                    var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var pathNameSaved = "/saved/"
                    var pathFileSaved = pathUtil + pathNameSaved + o.name
                    var pathFileChange = pathUtil + pathNameSaved + name
                    val oldFolder = File(pathFileSaved)
                    val newFolder =
                        File(pathFileChange)
                    val success = oldFolder.renameTo(newFolder)
                    var repeatedName = false
                    if (success) {
                        for (i in 0 until listFolder.size) {
                            if (i != index && listFolder[i].name == name) {
                                repeatedName = true
                                break
                            }
                        }
                        if (repeatedName) {
                            fetchFolder()
                        } else {
                            listFolder[index].name = pathFileChange
                            adapter.notifyItemChanged(index)
                        }
                    }
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun onItemMore(index: Int, data: OBase) {
                var bottomMore = BottomMore(object : BottomMoreEvent {
                    override fun onMove() {

                    }

                    override fun onCopy() {

                    }

                    override fun onShare() {

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
            for (j in files[i].listFiles()) {
                if (j.name.endsWith(JPG)) {
                    listPath.add(j.path)
                }
            }
            if (listPath.size == 0) {
                files[i].delete()
                continue
            }
            list.add(ImageFolder(files[i].name, files[i].name.substring(4, 15), listPath))
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
                if (!data.data!!) {
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
}