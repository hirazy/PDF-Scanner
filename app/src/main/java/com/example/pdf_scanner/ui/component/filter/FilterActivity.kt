package com.example.pdf_scanner.ui.component.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivityFilterBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : BaseActivity() {

    lateinit var binding: ActivityFilterBinding
    val viewModel: FilterViewModel by viewModels()

    override fun initViewBinding() {
        binding = ActivityFilterBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}