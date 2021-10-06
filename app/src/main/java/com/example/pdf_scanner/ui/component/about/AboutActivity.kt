package com.example.pdf_scanner.ui.component.about

import android.content.pm.PackageManager
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

        binding.tbAboutUs.setNavigationOnClickListener {
            finish()
        }

        binding.tvAboutVersion.text = getVersion()

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }

    private fun getVersion(): String {
        return try {
            "v " + packageManager
                .getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "v 1.0.0"
        }
    }
}