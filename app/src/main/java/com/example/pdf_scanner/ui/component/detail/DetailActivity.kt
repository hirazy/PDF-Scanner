package com.example.pdf_scanner.ui.component.detail

import android.app.Dialog
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.example.pdf_scanner.KEY_DATA_DETAIL
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.databinding.ActivityDetailBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.toObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var data: ImageFolder

    override fun initViewBinding() {
        binding = ActivityDetailBinding.inflate(layoutInflater)

        setSupportActionBar(binding.tbDetail)

        data = intent.getStringExtra(KEY_DATA_DETAIL)!!.toObject()

        binding.tvListImage.text = data.name

        binding.layoutCommentDetail.setOnClickListener {

        }

        binding.layoutEmailDetail.setOnClickListener {

        }

        binding.layoutShareDetail.setOnClickListener {

        }

        binding.layoutSaveDetail.setOnClickListener {
            val fileRoot = FileUtil(this@DetailActivity).getRootFolder()


        }

        binding.layoutPrintDetail.setOnClickListener {

        }

        binding.layoutTextDetail.setOnClickListener {
            var dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_rename)

            var edtFileName = dialog.findViewById<EditText>(R.id.edtReName)
            edtFileName.hint = data.name

            var cancelBtn = dialog.findViewById<Button>(R.id.btnDeclineReName)
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            var acceptBtn = dialog.findViewById<Button>(R.id.btnAcceptRename)
            acceptBtn.setOnClickListener {

            }
            dialog.show()
        }

        setContentView(binding.root)
    }

    override fun observeViewModel() {

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

    override fun onBackPressed() {

        super.onBackPressed()
    }

}