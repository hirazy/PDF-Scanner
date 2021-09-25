package com.example.pdf_scanner.ui.component.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.Q)
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

        binding.layoutTextSignSize.setOnClickListener {
            setTextSize()
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

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setTextSize() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text_size)

        var numberPicker = dialog.findViewById<NumberPicker>(R.id.numberPickerTextSize)
        numberPicker.isSelected = false
        numberPicker.maxValue = 10
        numberPicker.minValue = 50
        numberPicker.wrapSelectorWheel = false
        numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        var btnCancel = dialog.findViewById<Button>(R.id.btnCancelTextSize)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        var btnAccept = dialog.findViewById<Button>(R.id.btnSetTextSize)
        btnAccept.setOnClickListener {
            viewModel.setTextSize(numberPicker.value)
            binding.tvTextSignSize.text = numberPicker.value.toString()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun handleCamera(data: Resource<Boolean>) {
        when (data) {
            is Resource.Success -> {
                binding.layoutStartCamera.isChecked = data.data!!
                binding.layoutStartCamera.setOnCheckedChangeListener { buttonView, isChecked ->
                    viewModel.changeStartCamera(isChecked)
                }
            }
        }
    }

    private fun handleTextSize(data: Resource<Int>) {
        when (data) {
            is Resource.Success -> {
                binding.tvTextSignSize.text = data.data.toString()
            }
        }
    }

    override fun observeViewModel() {
        observe(viewModel.liveStartedCamera, ::handleCamera)
        observe(viewModel.liveTextSizeEdit, ::handleTextSize)
    }

}