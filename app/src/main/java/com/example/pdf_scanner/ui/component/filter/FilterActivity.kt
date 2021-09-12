package com.example.pdf_scanner.ui.component.filter

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_scanner.KEY_DATA_FILTER
import com.example.pdf_scanner.KEY_FILTER
import com.example.pdf_scanner.KEY_RESULT_FILTER
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.DataFilter
import com.example.pdf_scanner.data.dto.DataResultFilter
import com.example.pdf_scanner.data.dto.ImageFilter
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityFilterBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.filter.adapter.ImageFilterAdapter
import com.example.pdf_scanner.utils.toObject
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
    var path: String = ""
    lateinit var data: DataFilter

    override fun initViewBinding() {
        binding = ActivityFilterBinding.inflate(layoutInflater)

        data = intent.getStringExtra(KEY_DATA_FILTER)!!.toObject<DataFilter>()

        /**
         * Path image
         */
        path = data.path

        if(!data.isFilterAll){
            binding.layoutFilterAll.visibility = View.GONE
        }

        binding.layoutFilterCancel.setOnClickListener {
            finish()
        }

        binding.layoutFilterDone.setOnClickListener {
            filterDone()
        }

        binding.btnFilterNormal.setOnClickListener {
            // Update Normal
            for(i in 0 until list.size){
                list[i].isSelected = false
            }
            mPhotoEditor.setFilterEffect(PhotoFilter.NONE)
        }

        mPhotoEditor = PhotoEditor.Builder(this, binding.imgFilter)
            .setPinchTextScalable(true)
            .build()

        var file = File(path)
        val uri: Uri = Uri.fromFile(file)
        binding.imgFilter.source.setImageURI(uri)


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
        }, this)

        list = ArrayList()

        list.add(ImageFilter(path, PhotoFilter.BLACK_WHITE))
        list.add(ImageFilter(path, PhotoFilter.GRAIN))
        list.add(ImageFilter(path, PhotoFilter.GRAIN))
        list.add(ImageFilter(path, PhotoFilter.FISH_EYE))

        adapter.setData(list)
        binding.rcclvFilter.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcclvFilter.adapter = adapter

        setContentView(binding.root)
    }

    private fun filterDone(){
        var intent = Intent()
        var isFiltered = false
        for(i in 0 until list.size){
            if(list[i].isSelected){
                intent.putExtra(KEY_FILTER, DataResultFilter(list[i].filter, binding.swFilterAll.isEnabled).toJSON())
                isFiltered = true
                break
            }
        }
        if(!isFiltered){
            intent.putExtra(KEY_FILTER, DataResultFilter(PhotoFilter.NONE, binding.swFilterAll.isEnabled).toJSON())
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun observeViewModel() {

    }
}