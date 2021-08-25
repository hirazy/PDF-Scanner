package com.example.pdf_scanner.ui.component.main

import android.Manifest
import android.R.attr.path
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camerakit.CameraKitView
import com.camerakit.type.CameraFlash
import com.example.pdf_scanner.*
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataImage
import com.example.pdf_scanner.data.dto.DataScan
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.data.dto.OptionCamera
import com.example.pdf_scanner.databinding.ActivityMainBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.history.HistoryActivity
import com.example.pdf_scanner.ui.component.image.ImageActivity
import com.example.pdf_scanner.ui.component.main.adapter.OptionCameraAdapter
import com.example.pdf_scanner.ui.component.purchase.PurchaseActivity
import com.example.pdf_scanner.ui.component.scan.ScanActivity
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.SingleEvent
import com.kaopiz.kprogresshud.KProgressHUD
import com.oneadx.vpnclient.utils.observe
import com.oneadx.vpnclient.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    private var mBounceAnimation: Animation? = null
    val viewModel: MainViewModel by viewModels()
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: OptionCameraAdapter
    var listImg = ArrayList<String>()
    var listBitMap = ArrayList<Bitmap>()
    private var hud: KProgressHUD? = null
    var statusCamera = 0 // FLASH OR AUTO-FLASH
    var statusOption = 0 // WHITEBOARD, SINGLE, BATCH,...


    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.colorApp)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)

        /*
            Create file save image
         */

        var filePath = FileUtil(this@MainActivity).getRootFolder() + "/saved"
        var fileSaved = File(filePath)
        if(!fileSaved.exists()){
            fileSaved.parentFile.mkdirs()
            fileSaved.createNewFile()
        }

        mBounceAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_bounce_start)
        binding.btnMainCapture.setOnClickListener {
            binding.btnMainCapture.startAnimation(mBounceAnimation)

            hud!!.show()

            binding.cameraKit.captureImage(object : CameraKitView.ImageCallback {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onImage(p0: CameraKitView?, p1: ByteArray?) {

                    val str = String(p1!!, StandardCharsets.UTF_8)
                    val bmp = BitmapFactory.decodeByteArray(p1, 0, p1!!.size)

                    var fileRoot = FileUtil(this@MainActivity).getRootFolder()
                    var cnt = 1

                    var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val strTime =  "Scan " + date.format(Date())

                    var filePath = "${fileRoot}/${strTime}"
                    var file = File(filePath)

                    file.parentFile.mkdirs()
                    file.createNewFile()

                    val stream = FileOutputStream(file)
                    stream.write(p1)
                    stream.flush()
                    stream.close()

                    listImg.add(filePath)

                    if (listImg.size == 1) {
                        binding.btnImage.visibility = View.GONE
                        binding.layoutBadgeImage.visibility = View.VISIBLE
                        binding.circleImg.setImageBitmap(bmp!!)
                        binding.btnDocument.setImageResource(R.drawable.ic_close)
                        if (statusOption == KEY_SINGLE) {
                            var intent = Intent(this@MainActivity, ScanActivity::class.java)
                            intent.putExtra(KEY_DATA_SCAN, DataScan(listImg, statusOption, false))
                            startActivity(intent)
                        } else {
                            binding.rcclvMain.isNestedScrollingEnabled = false
                        }
                    } else {

                    }
                    hud!!.dismiss()
                }
            })
        }

        hasStoragePermission(1)

        setSupportActionBar(binding.tbMain)
        binding.tbMain.title = "" // set Title
        supportActionBar!!.setDisplayShowTitleEnabled(false);

        binding.tbMain.setNavigationOnClickListener {
            var intent = Intent(this, PurchaseActivity::class.java)
            startActivity(intent)
        }

        binding.btnDocument.setOnClickListener {
            if (listImg.size > 0) {
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_leave_scan)

                var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }
                var btnLeave = dialog.findViewById<Button>(R.id.btnAcceptLeave)
                btnLeave.setOnClickListener {
                    listImg = ArrayList()
                    binding.btnDocument.setImageResource(R.drawable.ic_document)
                    binding.btnImage.visibility = View.VISIBLE
                    binding.layoutBadgeImage.visibility = View.GONE
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                var intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnImage.setOnClickListener {
            var intent = Intent(this, ImageActivity::class.java)
            intent.putExtra(KEY_INTENT_IMAGE, DataImage(statusOption).toJSON())
            startActivity(intent)
        }

        binding.layoutBadgeImage.setOnClickListener {
            var intent = Intent(this, ScanActivity::class.java)
            if (statusOption == KEY_OCR) {
                intent.putExtra(KEY_DATA_SCAN, DataScan(listImg, statusOption, true).toJSON())
            } else {
                intent.putExtra(KEY_DATA_SCAN, DataScan(listImg, statusOption, false).toJSON())
            }
            startActivity(intent)
        }

        adapter = OptionCameraAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
                layoutManager.scrollToPosition(index)
            }

            override fun onOption(index: Int, data: OBase) {

            }
        })

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rcclvMain.layoutManager = layoutManager
        binding.rcclvMain.adapter = adapter

        binding.rcclvMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                binding.rcclvMain.post {
                    selectMiddleItem()
                }
            }
        })


        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var screenWidth = displayMetrics.widthPixels

        //      binding.rcclvMain.setPadding(screenWidth/2 - 60, 0, screenWidth/2 - 60, 0)

        setContentView(binding.root)
    }

    private fun saveImage() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun hasStoragePermission(requestCode: Int): Boolean {

        var permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
                false
            } else {
                true
            }
        } else {
            true
        }
    }


    fun BitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun selectMiddleItem() {
        val firstVisibleIndex = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleIndex = layoutManager.findLastVisibleItemPosition()
        val visibleIndexes = listOf(firstVisibleIndex..lastVisibleIndex).flatten()

        for (i in visibleIndexes) {
            val vh = binding.rcclvMain.findViewHolderForLayoutPosition(i)
            if (vh?.itemView == null) {
                continue
            }

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            var screenWidth = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels

            val location = IntArray(2)
            vh.itemView.getLocationOnScreen(location)
            val x = location[0]
            val halfWidth = vh.itemView.width * .5
            val rightSide = x + halfWidth
            val leftSide = x - halfWidth
            val isInMiddle = screenWidth * .5 in leftSide..rightSide
            if (isInMiddle) {
                // "i" is your middle index and implement selecting it as you want
                // optionsAdapter.selectItemAtIndex(i)
                viewModel.selectItem(position = i)
                statusOption = i
                return
            }
        }
    }

    override fun observeViewModel() {
        observe(viewModel.listOption, ::handleOption)
        observeEvent(viewModel.toastLiveData, ::handleToast)
    }

    private fun handleOption(list: Resource<ArrayList<OptionCamera>>) {
        when (list) {
            is Resource.Success -> {
                adapter.setData(list.data!!)
            }
        }
    }

    private fun handleToast(msg: SingleEvent<String>) {
        Toast.makeText(this@MainActivity, msg.peekContent(), Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemCapture -> {

            }

            R.id.itemFlash -> {
                Log.e("onOptionsItemSelected", "sa")
                when (statusCamera) {
                    0 -> {
                        statusCamera = 1
                        binding.tbMain.menu.findItem(R.id.itemFlash).icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_flash_on)
                        binding.cameraKit.flash = CameraFlash.ON.ordinal
                    }

                    1 -> {
                        statusCamera = 2
                        binding.tbMain.menu.findItem(R.id.itemFlash).icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_light)
                        binding.cameraKit.flash = CameraFlash.AUTO.ordinal
                    }

                    2 -> {
                        statusCamera = 0
                        binding.tbMain.menu.findItem(R.id.itemFlash).icon =
                            ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
                        binding.cameraKit.flash = CameraFlash.OFF.ordinal
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        binding.cameraKit.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.cameraKit.onResume()
    }

    override fun onPause() {
        binding.cameraKit.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.cameraKit.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.cameraKit.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}