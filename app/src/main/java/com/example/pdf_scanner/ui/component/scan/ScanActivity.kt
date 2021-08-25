package com.example.pdf_scanner.ui.component.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.example.pdf_scanner.*
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.dto.DataImage
import com.example.pdf_scanner.data.dto.DataScan
import com.example.pdf_scanner.databinding.ActivityScanBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.component.image.ImageActivity
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScan
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScanEvent
import com.example.pdf_scanner.ui.component.scan.dialog.ShapeBSFragment
import com.example.pdf_scanner.ui.component.scan.dialog.TextEditorDialogFragment
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.toObject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ScanActivity : BaseActivity(), ShapeBSFragment.Properties {

    lateinit var binding: ActivityScanBinding

    // lateinit var adapterImg: ImagePageAdapter
    lateinit var listImg: ArrayList<String>
    lateinit var dataScan: DataScan
    lateinit var handler: Handler
    var optionSelected = 0
    lateinit var adapterPager: ImageStateAdapter
    lateinit var onEdit: onEditPhoto

    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_app)
        }

        dataScan = intent.getStringExtra(KEY_DATA_SCAN)!!.toObject() as DataScan


        listImg = dataScan.listImg

        binding = ActivityScanBinding.inflate(layoutInflater)

        binding.tvCountPage.text = "1 / " + listImg.size.toString()

        setSupportActionBar(binding.tbScan)

        binding.tbScan.setNavigationIcon(R.drawable.ic_back)

        binding.tbScan.setNavigationOnClickListener {
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
                finish()
            }
            dialog.show()
        }

//        adapterImg = ImagePageAdapter(listImg, this, object : ImagePageListener {
//            override fun showTextEditor(inputText: String, colorCode: Int) {
//                val textEditorDialogFragment: TextEditorDialogFragment =
//                    TextEditorDialogFragment.show(this@ScanActivity, inputText, colorCode)
//                textEditorDialogFragment.setOnTextEditorListener(object :
//                    TextEditorDialogFragment.TextEditor {
//                    override fun onDone(inputText: String?, colorCode: Int) {
//
//                    }
//                })
//            }
//        })

        adapterPager = ImageStateAdapter(supportFragmentManager, listImg)
        binding.vpgImg.adapter = adapterPager

        // binding.vpgImg.adapter = adapterImg

        binding.layoutAddImage.setOnClickListener {
            var bottomDialog = BottomScan(object : BottomScanEvent {
                override fun takePhoto() {
                    finish()
                }

                override fun selectAlbum() {
                    var intent = Intent(this@ScanActivity, ImageActivity::class.java)
                    intent.putExtra(KEY_INTENT_IMAGE, DataImage(dataScan.status).toJSON())
                    startActivity(intent)
                }

            })
            bottomDialog.show(supportFragmentManager, bottomDialog.tag)
        }

        binding.animScan.setAnimation(R.raw.animation_scanner)
        binding.animScan.playAnimation()

        handler = Handler()
        handler.postDelayed({
            // adapterImg.setDocument()
            binding.animScan.cancelAnimation()
            binding.animScan.visibility = View.GONE
        }, 4000)

        binding.layoutRotateImage.setOnClickListener {

        }

        binding.layoutCropImage.setOnClickListener {


        }

        binding.layoutSignImage.setOnClickListener {
            // adapterImg.setModeShape()
            var dialog = ShapeBSFragment()
            showBottomSheetDialogFragment(dialog)
            binding.tbScan.title = SIGN
            dialog.setPropertiesChangeListener(this)
        }

        binding.layoutText.setOnClickListener {
            var dialogSign: TextEditorDialogFragment = TextEditorDialogFragment.show(
                this
            )

            dialogSign.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {
                override fun onDone(inputText: String?, colorCode: Int) {
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    // adapterImg.addText(inputText!!, styleBuilder)
                    Log.e("dialogSign", inputText!!)
                    onEdit.addText(inputText!!, styleBuilder)
                }
            })

        }

        binding.layoutEraser.setOnClickListener {
            binding.tbScan.title = ERASER
            // adapterImg.eraser()
        }

        binding.layoutDeleteImage.setOnClickListener {
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
                dialog.dismiss()
                finish()
            }
            dialog.show()
        }

        binding.btnPageBack.setOnClickListener {
            // var pos = adapterImg.getItemPosition()
            var pos = binding.vpgImg.currentItem
            if (pos >= 1) {
                binding.vpgImg.setCurrentItem(binding.vpgImg.currentItem - 1, true)
            }
        }

        binding.btnPageNext.setOnClickListener {
            //  var pos = adapterImg.getItemPosition()
            var pos = binding.vpgImg.currentItem
//            if (pos < adapterImg.count - 1) {
//                binding.vpgImg.setCurrentItem(binding.vpgImg.currentItem + 1, true)
//            }
        }

        binding.vpgImg.offscreenPageLimit = listImg.size

        binding.vpgImg.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                var pos = position + 1
                binding.tvCountPage.text = pos.toString() + " / " + listImg.size
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        setContentView(binding.root)
    }

    interface onEditPhoto {


        fun addText(inputText: String, styleBuilder: TextStyleBuilder)

        fun save()
    }

    @JvmName("setOnEdit1")
    fun setOnEdit(o: onEditPhoto) {
        onEdit = o
    }

    override fun observeViewModel() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (optionSelected == 0) {
            menuInflater.inflate(R.menu.item_action_scan, menu)
        } else {
            menuInflater.inflate(R.menu.item_action_ocr, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionScan -> {
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_save)

                var animSave = dialog.findViewById<LottieAnimationView>(R.id.animSave)
                animSave.setAnimation(R.raw.save_file)
                animSave.playAnimation()

                var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val strTime = "Scan " + date.format(Date())

                // adapterImg.save(strTime)


                dialog.show()
            }

            R.id.actionUndo -> {
                // adapterImg.unDo()
            }

            R.id.actionRedo -> {
                //adapterImg.reDo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onColorChanged(colorCode: Int) {
        Log.e("colorCode", colorCode.toString())
        //adapterImg.changeColorShape(colorCode)
    }

    override fun onOpacityChanged(opacity: Int) {
        Log.e("opacity", opacity.toString())
        // adapterImg.changeOpacity(opacity)
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        Log.e("shapeSize", shapeSize.toString())
        //  adapterImg.changeShapeSized(shapeSize)
    }

    override fun onShapePicked(shapeType: ShapeType?) {
        // adapterImg.shapePicked(shapeType!!)
    }

    class ImageStateAdapter(fm: FragmentManager, val list: ArrayList<String>) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Fragment {
            Log.e("ImageStateAdapter", position.toString())
            return ImageFragment(list).newInstance(position)!!
        }

        fun addText(pos: Int, inputText: String, styleBuilder: TextStyleBuilder) {

        }
    }

    class ImageFragment(val list: ArrayList<String>) : ListFragment(), OnPhotoEditorListener,
        onEditPhoto {

        lateinit var mPhotoEditor: PhotoEditor
        lateinit var mPhotoEditorView: PhotoEditorView
        lateinit var mShapeBuilder: ShapeBuilder
        var position = 0
        var mNum: Int = 0

        fun newInstance(number: Int): ImageFragment? {
            val args = Bundle()
            args.putInt("number", number)
            val fragment = ImageFragment(list)
            fragment.arguments = args
            position = number
            return fragment
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.e("onCreate", mNum.toString())
            mNum = if (arguments != null) requireArguments().getInt("number") else 1
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val root: View = inflater.inflate(R.layout.item_page_image, container, false)
            val number = requireArguments().getInt("number")
            mPhotoEditorView = root!!.findViewById(R.id.photoEditor)

            var file = File(list[number])

            if (file.exists()) {
                val uri: Uri = Uri.fromFile(file)
                mPhotoEditorView!!.source.setImageURI(uri)
            }

            mPhotoEditor = PhotoEditor.Builder(context, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build()
            mPhotoEditor!!.setOnPhotoEditorListener(this)
            mPhotoEditor!!.setFilterEffect(PhotoFilter.BLACK_WHITE)
            mShapeBuilder = ShapeBuilder()
            mPhotoEditor!!.setShape(mShapeBuilder)


            var act = activity as ScanActivity
            act.setOnEdit(object : onEditPhoto {
                override fun addText(inputText: String, styleBuilder: TextStyleBuilder) {
                    if (mNum == requireArguments().getInt("number")) {
                        mPhotoEditor!!.addText(inputText, styleBuilder)
                    }
                }

                override fun save() {

                }
            })

            container!!.addView(root)

            return root
        }

        fun addText(pos: Int, inputText: String, styleBuilder: TextStyleBuilder) {
            if (pos == requireArguments().getInt("num")) {
                Log.e("addText", "")
                mPhotoEditor!!.addText(inputText, styleBuilder)
            }
        }

        fun unDo() {
            mPhotoEditor!!.undo()
        }

        fun reDo() {
            mPhotoEditor!!.redo()
        }

        fun eraser() {
            mPhotoEditor!!.brushEraser()
        }

        fun setDocument() {
            // mPhotoEditor!!.setFilterEffect(PhotoFilter.DOCUMENTARY)
        }


        fun setModeShape() {
            mPhotoEditor!!.setBrushDrawingMode(true)
        }

        fun changeColorShape(colorCode: Int) {
            mPhotoEditor!!.setShape(mShapeBuilder.withShapeColor(colorCode))
        }

        fun changeOpacity(opacity: Int) {
            mPhotoEditor!!.setShape(mShapeBuilder.withShapeOpacity(opacity))
        }

        fun changeShapeSized(shapeSize: Int) {
            mPhotoEditor!!.setShape(mShapeBuilder.withShapeSize(shapeSize.toFloat()))
        }

        fun shapePicked(shapeType: ShapeType) {
            mPhotoEditor!!.setShape(mShapeBuilder.withShapeType(shapeType))
        }

        fun save(folderName: String) {
            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()

            var fileRoot = FileUtil(requireContext()).getRootFolder()
            var filePath = "$fileRoot/saved/$folderName"

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mPhotoEditor!!.saveAsFile(filePath, saveSettings, object : PhotoEditor.OnSaveListener {
                override fun onSuccess(imagePath: String) {
                    // pos is tail
                    if (requireArguments().getInt("number") == list.size - 1) {

                    }
                }

                override fun onFailure(exception: Exception) {

                }
            })
        }

        override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {

        }

        override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

        }

        override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

        }

        override fun onStartViewChangeListener(viewType: ViewType?) {

        }

        override fun onStopViewChangeListener(viewType: ViewType?) {

        }

        override fun onTouchSourceImage(event: MotionEvent?) {

        }

        override fun addText(inputText: String, styleBuilder: TextStyleBuilder) {
            Log.e("addText", inputText.toString())
            mPhotoEditor.addText(inputText, styleBuilder)
        }

        override fun save() {

        }
    }


}