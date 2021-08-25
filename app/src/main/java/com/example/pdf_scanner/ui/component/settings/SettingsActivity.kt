package com.example.pdf_scanner.ui.component.settings

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivitySettingsBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    lateinit var binding : ActivitySettingsBinding
    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.colorApp)
        }

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setSupportActionBar(binding.tbSettings)

        binding.tbSettings.setNavigationOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }

}