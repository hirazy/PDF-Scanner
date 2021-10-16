package com.example.pdf_scanner.ui.component.filter

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.KEY_DATA_FILTER
import com.example.pdf_scanner.KEY_FILTER
import com.example.pdf_scanner.data.dto.DataFilter
import com.example.pdf_scanner.data.dto.DataResultFilter
import com.example.pdf_scanner.data.dto.ImageFilter
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityFilterBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.filter.adapter.ImageFilterAdapter
import com.example.pdf_scanner.utils.toObject
import com.kaopiz.kprogresshud.KProgressHUD
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import java.io.File

@AndroidEntryPoint
class FilterActivity : BaseActivity() {

    lateinit var binding: ActivityFilterBinding
    lateinit var adapter: ImageFilterAdapter
    val viewModel: FilterViewModel by viewModels()
    lateinit var mPhotoEditor: PhotoEditor
    lateinit var list: ArrayList<ImageFilter>
    private var hud: KProgressHUD? = null
    var path: String = ""
    lateinit var data: DataFilter

    override fun initViewBinding() {
        binding = ActivityFilterBinding.inflate(layoutInflater)

        data = intent.getStringExtra(KEY_DATA_FILTER)!!.toObject<DataFilter>()

        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)


        /**
         * Path image
         */
        path = data.path

        if(!data.isFilterAll){
            binding.layoutFilterAll1.visibility = View.GONE
        }

        binding.layoutFilterCancel1.setOnClickListener {
            finish()
        }

        binding.layoutFilterDone1.setOnClickListener {
            filterDone()
        }

        binding.btnFilterNormal1.setOnClickListener {
            // Update Normal
            for(i in 0 until list.size){
                list[i].isSelected = false
            }
            mPhotoEditor.setFilterEffect(PhotoFilter.NONE)
        }

        mPhotoEditor = PhotoEditor.Builder(this, binding.imgFilter1)
            .setPinchTextScalable(true)
            .build()

        var file = File(path)
        val uri: Uri = Uri.fromFile(file)
        binding.imgFilter1.source.setImageURI(uri)


        adapter = ImageFilterAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
                var o = data as ImageFilter
                if(!o.isSelected){
                    mPhotoEditor.setFilterEffect(o.filter)
                    for(i in 0 until list.size){
                        if(list[i].isSelected){
                            list[i].isSelected = false
                            break
                        }
                    }
                }
                list[index].isSelected = true
            }

            override fun onOption(index: Int, data: OBase) {

            }
        }, uri, this)

        list = ArrayList()

        list.add(ImageFilter(path, PhotoFilter.BLACK_WHITE))
        list.add(ImageFilter(path, PhotoFilter.GRAIN))
        list.add(ImageFilter(path, PhotoFilter.GRAIN))
        list.add(ImageFilter(path, PhotoFilter.FISH_EYE))

        adapter.setData(list)
        binding.rcclvFilter1.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcclvFilter1.adapter = adapter

        setContentView(binding.root)
    }

    private fun filterDone(){
        var intent = Intent()
        var isFiltered = false
        for(i in 0 until list.size){
            if(list[i].isSelected){
                intent.putExtra(KEY_FILTER, DataResultFilter(list[i].filter, binding.swFilterAll1.isChecked).toJSON())
                isFiltered = true
                break
            }
        }
        if(!isFiltered){
            intent.putExtra(KEY_FILTER, DataResultFilter(PhotoFilter.NONE, binding.swFilterAll1.isChecked).toJSON())
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun observeViewModel() {

    }
}