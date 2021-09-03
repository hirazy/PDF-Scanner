package com.example.pdf_scanner.ui.component.detail_text

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivityDetailTextBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailTextActivity : BaseActivity() {

    lateinit var binding: ActivityDetailTextBinding

    override fun initViewBinding() {
        binding = ActivityDetailTextBinding.inflate(layoutInflater)


        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}