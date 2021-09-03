package com.example.pdf_scanner.ui.component.select

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.KEY_DATA_SELECT
import com.example.pdf_scanner.data.dto.DataSelect
import com.example.pdf_scanner.data.dto.FolderSelect
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivitySelectBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.select.adapter.SelectFolderAdapter
import com.example.pdf_scanner.utils.toObject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectActivity : BaseActivity() {

    lateinit var binding: ActivitySelectBinding
    val viewModel: SelectViewModel by viewModels()
    lateinit var adapter: SelectFolderAdapter
    var listFolder =  ArrayList<FolderSelect>()

    override fun initViewBinding() {
        binding = ActivitySelectBinding.inflate(layoutInflater)

        fetchData(intent.getStringExtra(KEY_DATA_SELECT)!!.toObject<DataSelect>().list)

        adapter = SelectFolderAdapter(object: RecyclerItemListener{
            override fun onItemSelected(index: Int, data: OBase) {

            }

            override fun onOption(index: Int, data: OBase) {

            }

        })

        binding.rcclvSelectFolder.layoutManager = LinearLayoutManager(this)
        binding.rcclvSelectFolder.adapter = adapter

        setContentView(binding.root)
    }

    private fun fetchData(list: ArrayList<ImageFolder>){
        val noSelected = false
        for(i in 0 until list.size){
            listFolder.add(FolderSelect(noSelected, list[i]))
        }
        viewModel.fetchFolder(listFolder)
    }

    override fun observeViewModel() {

    }
}