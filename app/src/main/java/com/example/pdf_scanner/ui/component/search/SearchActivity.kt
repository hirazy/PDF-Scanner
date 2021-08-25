package com.example.pdf_scanner.ui.component.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivitySearchBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding

    var list = ArrayList<String>()

    override fun initViewBinding() {
        binding = ActivitySearchBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}