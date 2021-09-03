package com.example.pdf_scanner.ui.component.history

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.KEY_DATA_DETAIL
import com.example.pdf_scanner.KEY_DATA_SEARCH
import com.example.pdf_scanner.KEY_DATA_SELECT
import com.example.pdf_scanner.R
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
import com.example.pdf_scanner.ui.component.search.SearchActivity
import com.example.pdf_scanner.ui.component.select.SelectActivity
import com.example.pdf_scanner.ui.component.settings.SettingsActivity
import com.example.pdf_scanner.utils.FileUtil
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class HistoryActivity : BaseActivity() {

    val viewModel: HistoryViewModel by viewModels()
    lateinit var binding: ActivityHistoryBinding
    lateinit var adapter: FolderAdapter
    lateinit var listFolder : ArrayList<ImageFolder>

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
            finish()
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

                }

                var btnCancel = dialog.findViewById<Button>(R.id.btnDeclineReName)
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                var btnAccept = dialog.findViewById<Button>(R.id.btnAcceptRename)
                btnAccept.setOnClickListener {
                    var name = nameFolder.text

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
        var filePath = FileUtil(this@HistoryActivity).getRootFolder() + "/saved"
        val directory = File(filePath)
        listFolder = ArrayList()
        if (!directory.exists() || directory.listFiles() == null) {
            return
        }
        val files: Array<File> = directory.listFiles()
        for (i in files.indices) {
            var listPath = ArrayList<String>()
            for (j in files[i].listFiles()) {
                listPath.add(j.path)
            }
            list.add(ImageFolder(files[i].name, files[i].name.substring(4, 14), listPath))
        }
        listFolder = list
        viewModel.fetchData(list)
    }

    override fun observeViewModel() {
        observe(viewModel.listLiveData, ::handleFolder)
    }

    private fun handleFolder(data: Resource<ArrayList<ImageFolder>>) {
        when (data) {
            is Resource.Success -> {
                adapter.setData(data.data!!)
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
}