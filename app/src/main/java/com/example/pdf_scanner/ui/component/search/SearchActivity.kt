package com.example.pdf_scanner.ui.component.search

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.KEY_DATA_DETAIL
import com.example.pdf_scanner.KEY_DATA_SEARCH
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataSearch
import com.example.pdf_scanner.data.dto.ImageFolder
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivitySearchBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.detail.DetailActivity
import com.example.pdf_scanner.ui.component.search.adapter.CardFolderAdapter
import com.example.pdf_scanner.utils.toObject
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding
    val viewModel: SearchViewModel by viewModels()
    lateinit var adapter: CardFolderAdapter
    var list = ArrayList<ImageFolder>()

    override fun initViewBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)

        list = intent.getStringExtra(KEY_DATA_SEARCH)!!.toObject<DataSearch>().list

        binding.btnDeleteSearch.setOnClickListener {

            finish()
        }

        binding.btnClearSearch.setOnClickListener {
            binding.edtSearch.setText("", TextView.BufferType.EDITABLE)
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        adapter = CardFolderAdapter(object: RecyclerItemListener{
            override fun onItemSelected(index: Int, data: OBase) {
                var intent = Intent(this@SearchActivity, DetailActivity::class.java)
                intent.putExtra(KEY_DATA_DETAIL, DataSearch(list))
                startActivity(intent)
            }

            override fun onOption(index: Int, data: OBase) {
            }
        })

        binding.rcclvSearch.layoutManager = LinearLayoutManager(this)
        binding.rcclvSearch.adapter = adapter

        viewModel.fetchData(list)
        setContentView(binding.root)
    }

    override fun observeViewModel() {
        observe(viewModel.listFolder, ::handleFolder)
    }

    private fun handleFolder(data: Resource<ArrayList<ImageFolder>>){
        when(data){
            is Resource.Success ->{
                adapter.setData(data.data!!)
            }
        }
    }
}