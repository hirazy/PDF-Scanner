package com.example.pdf_scanner.ui.component.ocr

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.*
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.LanguageOCR
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityOcractivityBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.ocr.adapter.LanguageAdapter
import com.oneadx.vpnclient.utils.observe
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OCRActivity : BaseActivity() {

    lateinit var binding: ActivityOcractivityBinding
    val viewModel: OCRViewModel by viewModels()
    lateinit var adapter: LanguageAdapter
    lateinit var listOCR: ArrayList<LanguageOCR>

    override fun initViewBinding() {
        binding = ActivityOcractivityBinding.inflate(layoutInflater)

        setSupportActionBar(binding.tbOCR)

        adapter = LanguageAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
            }

            override fun onOption(index: Int, data: OBase) {
                var o = data as LanguageOCR
                if (!o.isEnabled) {
                    var cnt = 0
                    for (i in 0 until listOCR.size) {
                        if (listOCR[i].isEnabled) {
                            cnt++
                        }
                    }
                    if (cnt == 3) {
                        DynamicToast.makeError(
                            this@OCRActivity,
                            "Tips: To ensure the accuracy of OCR, you can choose up to 3 languages."
                        ).show()
                    } else {
                        listOCR[index].isEnabled = true
                        adapter.notifyItemChanged(index)
                    }
                } else {
                    listOCR[index].isEnabled = false
                    adapter.notifyItemChanged(index)
                }
            }
        })

        binding.rcclvLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcclvLanguage.adapter = adapter

        binding.tbOCR.setNavigationOnClickListener {
            finish()
        }

        fetchOCR()

        setContentView(binding.root)
    }

    override fun observeViewModel() {
        observe(viewModel.listLanguage, ::handleLanguage)
    }

    private fun handleLanguage(data: Resource<ArrayList<LanguageOCR>>) {
        when (data) {
            is Resource.Success -> {
                adapter.setData(listOCR)
            }
        }
    }

    private fun fetchOCR() {
        listOCR = ArrayList<LanguageOCR>()
        listOCR.add(LanguageOCR(English, false))
        listOCR.add(LanguageOCR(Afrikaans, false))
        listOCR.add(LanguageOCR(Albanian, false))
        listOCR.add(LanguageOCR(Arabic, false))
        listOCR.add(LanguageOCR(Armenian, false))
        listOCR.add(LanguageOCR(Belorussian, false))
        listOCR.add(LanguageOCR(Bengali, false))
        listOCR.add(LanguageOCR(Bulgarian, false))
        listOCR.add(LanguageOCR(Catalan, false))
        listOCR.add(LanguageOCR(Chinese, false))
        listOCR.add(LanguageOCR(Croatian, false))
        listOCR.add(LanguageOCR(Czech, false))
        listOCR.add(LanguageOCR(Danish, false))
        listOCR.add(LanguageOCR(Dutch, false))
        listOCR.add(LanguageOCR(Estonian, false))
        listOCR.add(LanguageOCR(Filipino, false))
        listOCR.add(LanguageOCR(Finnish, false))
        listOCR.add(LanguageOCR(French, false))
        listOCR.add(LanguageOCR(German, false))
        listOCR.add(LanguageOCR(Greek, false))
        listOCR.add(LanguageOCR(Gujarati, false))
        listOCR.add(LanguageOCR(Hebrew, false))
        listOCR.add(LanguageOCR(Hindi, false))
        listOCR.add(LanguageOCR(Hungarian, false))
        listOCR.add(LanguageOCR(Icelandic, false))
        listOCR.add(LanguageOCR(Indonesian, false))
        listOCR.add(LanguageOCR(Italian, false))
        listOCR.add(LanguageOCR(Japanese, false))
        listOCR.add(LanguageOCR(Kannada, false))
        listOCR.add(LanguageOCR(Khmer, false))
        listOCR.add(LanguageOCR(Korean, false))
        listOCR.add(LanguageOCR(Lao, false))
        listOCR.add(LanguageOCR(Latvian, false))
        listOCR.add(LanguageOCR(Lithuanian, false))
        listOCR.add(LanguageOCR(Macedonian, false))
        listOCR.add(LanguageOCR(Malay, false))
        listOCR.add(LanguageOCR(Malayalam, false))
        listOCR.add(LanguageOCR(Marathi, false))
        listOCR.add(LanguageOCR(Nepali, false))
        listOCR.add(LanguageOCR(Norwegian, false))
        listOCR.add(LanguageOCR(Persian, false))
        listOCR.add(LanguageOCR(Polish, false))
        listOCR.add(LanguageOCR(Portuguese, false))
        listOCR.add(LanguageOCR(Punjabi, false))
        listOCR.add(LanguageOCR(Romanian, false))
        listOCR.add(LanguageOCR(Russian1, false))
        listOCR.add(LanguageOCR(Russian2, false))
        listOCR.add(LanguageOCR(Serbian1, false))
        listOCR.add(LanguageOCR(Serbian2, false))
        listOCR.add(LanguageOCR(Slovak, false))
        listOCR.add(LanguageOCR(Slovenian, false))
        listOCR.add(LanguageOCR(Spanish, false))
        listOCR.add(LanguageOCR(Swedish, false))
        listOCR.add(LanguageOCR(Tamil, false))
        listOCR.add(LanguageOCR(Telugu, false))
        listOCR.add(LanguageOCR(Thai, false))
        listOCR.add(LanguageOCR(Turkish, false))
        listOCR.add(LanguageOCR(Ukrainian, false))
        listOCR.add(LanguageOCR(Vietnamese, false))
        listOCR.add(LanguageOCR(Yiddish, false))
        viewModel.fetchLanguage(listOCR)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionSaveOCR -> {
                var intent = Intent()
                var languages = ArrayList<String>()
                for (i in 0 until listOCR.size)
                    if (listOCR[i].isEnabled) {
                        languages.add(listOCR[i].name)
                    }
                viewModel.saveLanguage(languages.toSet())
                setResult(RESULT_OK)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_ocr_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

}