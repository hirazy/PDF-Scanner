package com.example.pdf_scanner.ui.component.settings

import android.content.Intent
import android.os.Build
import android.view.Window
import androidx.activity.viewModels
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.databinding.ActivitySettingsBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.component.about.AboutActivity
import com.example.pdf_scanner.ui.component.purchase.PurchaseActivity
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    lateinit var binding: ActivitySettingsBinding
    val viewModel: SettingsViewModel by viewModels()

    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.colorApp)
        }

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        setSupportActionBar(binding.tbSettings)

        binding.layoutPremium.setOnClickListener {
            var intent = Intent(this@SettingsActivity, PurchaseActivity::class.java)
            startActivity(intent)
        }

        binding.layoutFeedBack.setOnClickListener {

        }

        binding.layoutAboutUs.setOnClickListener {
            var intent = Intent(this@SettingsActivity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.tbSettings.setNavigationOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    private fun handleCamera(data: Resource<Boolean>){
        when(data){
            is Resource.Success -> {

            }
        }
    }

    override fun observeViewModel() {
        observe(viewModel.liveStartedCamera, ::handleCamera)
    }

}