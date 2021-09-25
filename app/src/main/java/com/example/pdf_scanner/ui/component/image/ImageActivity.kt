package com.example.pdf_scanner.ui.component.image

import android.content.Intent
import android.database.Cursor
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdf_scanner.KEY_DATA_SCAN
import com.example.pdf_scanner.KEY_INTENT_IMAGE
import com.example.pdf_scanner.KEY_SINGLE
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataImage
import com.example.pdf_scanner.data.dto.DataScan
import com.example.pdf_scanner.data.dto.ImageCard
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityImageBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.image.adapter.CardImageAdapter
import com.example.pdf_scanner.ui.component.image.adapter.CardImageSelectedAdapter
import com.example.pdf_scanner.ui.component.scan.ScanActivity
import com.example.pdf_scanner.utils.toObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ImageActivity : BaseActivity() {

    lateinit var binding: ActivityImageBinding

    var list = ArrayList<ImageCard>()
    private val viewModel: ImageViewModel by viewModels()
    var listSelected = ArrayList<ImageCard>()
    lateinit var dataO: DataImage
    lateinit var adapter: CardImageAdapter
    lateinit var adapterSelected: CardImageSelectedAdapter
    private var hudProgress: KProgressHUD? = null
    var isLoading = false

    override fun initViewBinding() {
        binding = ActivityImageBinding.inflate(layoutInflater)

        dataO = intent.getStringExtra(KEY_INTENT_IMAGE)!!.toObject()

        if (dataO.status == KEY_SINGLE) {
            binding.layoutImageSelected.visibility = View.GONE
        }

        binding.tbImage.setNavigationIcon(R.drawable.ic_back)

        binding.tbImage.setNavigationOnClickListener {
            finish()
        }

        hudProgress = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)

        fetchImage()

        adapter = CardImageAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {

                if (dataO.status == KEY_SINGLE) {
                    listSelected.add(data as ImageCard)
                    var intent = Intent(this@ImageActivity, ScanActivity::class.java)
                    var listImage = ArrayList<String>()
                    listImage.add(listSelected[0].path)
                    intent.putExtra(
                        KEY_DATA_SCAN,
                        DataScan(listImage, dataO.status, false).toJSON()
                    )
                    startActivity(intent)
                    finish()
                }

                if (list[index].countSelected.isEmpty()) {
                    listSelected.add(data as ImageCard)
                    adapterSelected.setValue(listSelected)
                    adapterSelected.notifyItemInserted(listSelected.size)
                    binding.rcclvImageSelected.scrollToPosition(listSelected.size - 1)
                    if (listSelected.size == 1)
                        binding.btnDoneImg.setBackgroundResource(R.drawable.bg_btn_done1)
                    var num = listSelected.size
                    var textCount = num.toString()
                    if (num < 10)
                        textCount = "0$textCount"
                    list[index].countSelected = textCount
                    adapter.notifyItemChanged(index)
                }
            }

            override fun onOption(index: Int, data: OBase) {

            }
        })
        binding.rcclvImage.layoutManager = GridLayoutManager(this, 4)
        binding.rcclvImage.adapter = adapter

        adapterSelected = CardImageSelectedAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {

            }

            override fun onOption(index: Int, data: OBase) {
                var o = data as ImageCard
                listSelected.removeAt(index)
                adapterSelected.notifyItemRemoved(index - 1)
                adapterSelected.setValue(listSelected)
                if (listSelected.size == 0)
                    binding.btnDoneImg.setBackgroundResource(R.drawable.bg_btn_done)

                var listIndex = ArrayList<Int>()
                var numberMiddle = 0

                for (i in 0 until list.size) {
                    if (list[i].path == o.path) {
                        numberMiddle = list[i].countSelected.toInt()
                        list[i].countSelected = ""
                        adapter.notifyItemChanged(i)
                        continue
                    }
                    if (list[i].countSelected.isNotEmpty()) {
                        listIndex.add(i)
                    }
                }

                for (i in 0 until listIndex.size) {
                    var num = list[listIndex[i]].countSelected.toInt()
                    if (num > numberMiddle) {
                        num -= 1
                        if (num < 10) {
                            list[listIndex[i]].countSelected = "0$num"
                        } else {
                            list[listIndex[i]].countSelected = num.toString()
                        }
                        adapter.notifyItemChanged(listIndex[i])
                    }
                }
            }
        })

        binding.rcclvImageSelected.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rcclvImageSelected.adapter = adapterSelected

        adapterSelected.setData(listSelected)

        binding.btnDoneImg.setOnClickListener {
            if (listSelected.size > 0) {
                var intent = Intent(this@ImageActivity, ScanActivity::class.java)
                var listImage = ArrayList<String>()
                for (i in 0 until listSelected.size) {
                    listImage.add(listSelected[i].path)
                }
                intent.putExtra(KEY_DATA_SCAN, DataScan(listImage, dataO.status, false).toJSON())
                startActivity(intent)
                finish()
            }
        }

        binding.rcclvImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rcclvImage.canScrollVertically(1)) {
                    loadMore()
                }
            }
        })

        setContentView(binding.root)
    }

    private fun fetchImage() {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
        val cursor: Cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, orderBy
        )!!
        val count: Int = cursor.count
        val arrPath = arrayOfNulls<String>(count)
        var list = ArrayList<String>()

        for (i in 0 until count) {
            cursor.moveToPosition(i)
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            arrPath[i] = cursor.getString(dataColumnIndex)
            list.add(arrPath[i]!!)
        }
        cursor.close()
        viewModel.fetchImage(ArrayList(list.reversed()))
    }

    private fun addMore() {
        var listAll = viewModel.listAll
        var ind = list.size
        for (i in ind until Math.min(ind + 20, listAll.size)) {
            list.add(listAll[i])
        }
        adapter.notifyItemRangeInserted(ind, list.size - ind)
    }

    private fun loadMore() {
        var listAll = viewModel.listAll
        if (listAll.size == list.size) {
            return
        }
        val handler = Handler()
        hudProgress!!.show()
        handler.postDelayed(Runnable {
            addMore()
            hudProgress!!.dismiss()
        }, 1000)
    }

    private fun observeListImage(listImg: Resource<ArrayList<ImageCard>>) {
        when (listImg) {
            is Resource.Success -> {
                list = listImg.data!!
                adapter.setData(list)
            }
        }
    }

    override fun observeViewModel() {
        observe(viewModel.list, ::observeListImage)
    }
}