package com.example.pdf_scanner.ui.component.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivityAboutBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : BaseActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun initViewBinding() {
        binding = ActivityAboutBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }
}