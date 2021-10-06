package com.example.pdf_scanner.ui.component.main

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
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
import com.example.pdf_scanner.ui.component.main.adapter.OptionAdapter
import com.example.pdf_scanner.ui.component.main.adapter.OptionCameraAdapter
import com.example.pdf_scanner.ui.component.purchase.PurchaseActivity
import com.example.pdf_scanner.ui.component.scan.ScanActivity
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.SingleEvent
import com.kaopiz.kprogresshud.KProgressHUD
import com.oneadx.vpnclient.utils.observe
import com.oneadx.vpnclient.utils.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author hirazy2001
 * Main Activity to select Option
 */

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    private var mBounceAnimation: Animation? = null
    val viewModel: MainViewModel by viewModels()
    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: OptionCameraAdapter
    var listImg = ArrayList<String>()
    private var hud: KProgressHUD? = null
    var statusCamera = 0 // FLASH OR AUTO-FLASH
    var statusOption = 0 // WHITEBOARD, SINGLE, BATCH,...
    var listOption = ArrayList<OptionCamera>()
    var filePath: String = ""
    private var isExitAgain: Boolean = false
    private lateinit var mExamplePagerAdapter: OptionAdapter


    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.statusBarColor = resources.getColor(R.color.colorApp)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        hud = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        /**
         *  Create file save image
         */
        var pathNameSaved = "/saved/"
        filePath = FileUtil(this@MainActivity).getRootFolder() + pathNameSaved
        var fileSaved = File(filePath)
        if (!fileSaved.exists()) {
            fileSaved.mkdirs()
            var pathNameSaved1 = "/saved/test.txt"
            var filePath1 = FileUtil(this@MainActivity).getRootFolder() + pathNameSaved
            var file1 = File(filePath1)
            file1.mkdir()
        }

        mBounceAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_bounce_start)

        binding.btnMainCapture.setOnClickListener {
            binding.btnMainCapture.startAnimation(mBounceAnimation)
            hud!!.show()
            binding.cameraKit.captureImage(object : CameraKitView.ImageCallback {
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onImage(p0: CameraKitView?, p1: ByteArray?) {

                    // val str = String(p1!!, StandardCharsets.UTF_8)
                    val bmp = getResizedBitmap(
                        BitmapFactory.decodeByteArray(p1, 0, p1!!.size),
                        RESOLUTION_WIDTH,
                        RESOLUTION_HEIGHT
                    )
                    val streamByte = ByteArrayOutputStream()
                    bmp!!.compress(Bitmap.CompressFormat.PNG, 100, streamByte)
                    val bmpArray = streamByte.toByteArray()
                    // val bmp = BitmapFactory.decodeByteArray(p1, 0, p1!!.size)
                    val fileRoot = FileUtil(this@MainActivity).getRootFolder()
                    var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val strTime = "Scan " + date.format(Date())

                    val filePath = "${fileRoot}/${strTime}$JPG"
                    var file = File(filePath)
                    file.createNewFile()
                    val stream = FileOutputStream(file)
                    stream.write(bmpArray)
                    stream.flush()
                    stream.close()
                    /**
                     * Add image file saved
                     */
                    listImg.add(filePath)
                    if (listImg.size == 1) {
                        binding.btnImage.visibility = View.GONE
                        binding.layoutBadgeImage.visibility = View.VISIBLE
                        binding.circleImg.setImageBitmap(bmp!!)
                        binding.btnDocument.setImageResource(R.drawable.ic_close)
                        binding.vpgMain.isActivated = false
                        if (statusOption == KEY_SINGLE) {
                            var intent = Intent(this@MainActivity, ScanActivity::class.java)
                            intent.putExtra(
                                KEY_DATA_SCAN,
                                DataScan(listImg, statusOption, false).toJSON()
                            )
                            startActivity(intent)
                        }
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
            onDocument()
        }
        binding.btnImage.setOnClickListener {
            onImage()
        }

        binding.layoutBadgeImage.setOnClickListener {
            onImageScan()
        }

        adapter = OptionCameraAdapter(object : RecyclerItemListener {
            override fun onItemSelected(index: Int, data: OBase) {
                layoutManager.scrollToPosition(index)
            }

            override fun onOption(index: Int, data: OBase) {

            }
        })

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        /**
         * Delete File Saved
         */
        deleteFileSaved()
        setContentView(binding.root)
    }

    private fun onDocument() {
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
            val isStartedCamera = viewModel.liveStartCamera.value!!.data
            if (isStartedCamera == true) {
                var intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            } else {
                finish()
            }
        }
    }

    private fun onImage() {
        var intent = Intent(this, ImageActivity::class.java)
        intent.putExtra(KEY_INTENT_IMAGE, DataImage(statusOption).toJSON())
        startActivity(intent)
    }

    private fun onImageScan() {
        var intent = Intent(this, ScanActivity::class.java)
        var status = binding.vpgM.currentItem
        if (statusOption == KEY_OCR) {
            intent.putExtra(KEY_DATA_SCAN, DataScan(listImg, status, true).toJSON())
        } else {
            intent.putExtra(KEY_DATA_SCAN, DataScan(listImg, status, false).toJSON())
        }
        startActivity(intent)
    }

    private fun deleteFileSaved() {
        var filePath = FileUtil(this@MainActivity).getRootFolder()
        var fileSaved = "$filePath/saved"
        val directory = File(filePath)
        if (!directory.exists() || directory.listFiles() == null) {
            return
        }

        var listFiles = directory.listFiles()
        for (i in listFiles.indices) {
            if (listFiles[i].path != fileSaved) {
                deleteFilePath(listFiles[i].path)
            }
        }
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
    }

    private fun deleteFilePath(path: String) {
        val file: File = File(path)
        file.delete()
        if (file.exists()) {
            file.canonicalFile.delete()
            if (file.exists()) {
                applicationContext.deleteFile(file.name)
            }
        }
    }

    private fun initViewpager() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isSkimOver = true
        val padding = UIUtil.getScreenWidth(this) / 2
        commonNavigator.rightPadding = padding
        commonNavigator.leftPadding = padding
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return listOption.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = listOption[index].name
                clipPagerTitleView.textColor = Color.parseColor("#4C4C4C")
                clipPagerTitleView.clipColor = Color.RED
                clipPagerTitleView.setOnClickListener {
                    if (index != binding.vpgM.currentItem) {
                        statusOption = index
                        binding.vpgM.setCurrentItem(index, true)
                        var msgStatus = ""
                        when (index) {
                            KEY_WHITEBOARD -> {
                                msgStatus = TOAST_WHITEBOARD
                            }
                            KEY_OCR -> {
                                msgStatus = TOAST_OCR
                            }
                            KEY_SINGLE -> {
                                msgStatus = TOAST_SINGLE
                            }
                            KEY_BATCH -> {
                                msgStatus = TOAST_BATCH
                            }
                            KEY_CARD -> {
                                msgStatus = TOAST_CARD
                            }
                        }
                        viewModel.showToast(msgStatus)
                    }
                }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                return null
            }
        }
        binding.vpgMain.navigator = commonNavigator
        ViewPagerHelper.bind(binding.vpgMain, binding.vpgM)
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

    override fun observeViewModel() {
        observe(viewModel.listOption, ::handleOption)
        observeEvent(viewModel.toastLiveData, ::handleToast)
    }

    private fun handleOption(list: Resource<ArrayList<OptionCamera>>) {
        when (list) {
            is Resource.Success -> {
                adapter.setData(list.data!!)
                listOption = list.data!!
                mExamplePagerAdapter = OptionAdapter(listOption)
                binding.vpgM.adapter = mExamplePagerAdapter
                initViewpager()
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

    override fun onBackPressed() {
        if (isExitAgain) {
            super.onBackPressed()
        }

        var isStartedCamera = viewModel.liveStartCamera.value!!.data
        if (isStartedCamera == true) {
            isExitAgain = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed(
                Runnable
                { isExitAgain = false }, 2000
            )
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // binding.cameraKit.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}