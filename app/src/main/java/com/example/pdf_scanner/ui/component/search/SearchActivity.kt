package com.example.pdf_scanner.ui.component.search

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.viewModels
import com.example.pdf_scanner.databinding.ActivitySearchBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding
    val viewModel: SearchViewModel by viewModels()

    var list = ArrayList<String>()

    override fun initViewBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)

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

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}