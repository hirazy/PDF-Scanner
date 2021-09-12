package com.example.pdf_scanner.ui.component.detail_text

import android.view.Menu
import android.view.MenuItem
import com.example.pdf_scanner.R
import com.example.pdf_scanner.databinding.ActivityDetailTextBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailTextActivity : BaseActivity() {

    lateinit var binding: ActivityDetailTextBinding

    override fun initViewBinding() {
        binding = ActivityDetailTextBinding.inflate(layoutInflater)


        setSupportActionBar(binding.tbDetailText)

        binding.tbDetailText.setNavigationOnClickListener {
            finish()
        }

        binding.vpgDetailText.offscreenPageLimit = 2

        setContentView(binding.root)
    }

    override fun observeViewModel() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_detail_text, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionSaveDetail -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}