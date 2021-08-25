package com.example.pdf_scanner.ui.component.purchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivityPurchaseBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseActivity : BaseActivity() {

    lateinit var binding: ActivityPurchaseBinding

    override fun initViewBinding() {
        binding = ActivityPurchaseBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }

}