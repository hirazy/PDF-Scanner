package com.example.pdf_scanner.ui.component.detail

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pdf_scanner.KEY_DATA_DETAIL
import com.example.pdf_scanner.KEY_DATA_DETAIL_TEXT
import com.example.pdf_scanner.R
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
import com.example.pdf_scanner.utils.toObject
import com.oneadx.vpnclient.utils.observe
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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
            shareFilePDF()
        }

        binding.layoutShareDetail.setOnClickListener {
            shareImage()
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

    private fun shareFilePDF() {
        val sharingIntent = Intent(Intent.ACTION_SEND)

    }

    private fun shareImage() {
        var bottomShare = BottomShare(object : BottomShareEvent {
            override fun sharePDF() {

            }

            override fun shareImage() {
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
                    val uri = Uri.fromFile(file)
                    files.add(uri)
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
                startActivity(Intent.createChooser(intent, "Share Image"))
            }

            override fun shareWord() {

            }

            override fun shareText() {

            }

            override fun dismiss() {

            }
        })
        bottomShare.show(supportFragmentManager, bottomShare.tag)
    }

    private fun printDetail() {

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

        var acceptBtn = dialog.findViewById<Button>(R.id.btnAcceptRename)
        acceptBtn.setOnClickListener {

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