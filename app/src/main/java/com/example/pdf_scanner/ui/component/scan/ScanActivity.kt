package com.example.pdf_scanner.ui.component.scan

import android.Manifest
import android.animation.Animator
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.example.pdf_scanner.*
import com.example.pdf_scanner.R
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.DataImage
import com.example.pdf_scanner.data.dto.DataLanguage
import com.example.pdf_scanner.data.dto.DataScan
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.databinding.ActivityScanBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener
import com.example.pdf_scanner.ui.component.filter.FilterActivity
import com.example.pdf_scanner.ui.component.image.ImageActivity
import com.example.pdf_scanner.ui.component.ocr.OCRActivity
import com.example.pdf_scanner.ui.component.scan.adapter.LanguageSelectedAdapter
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScan
import com.example.pdf_scanner.ui.component.scan.dialog.BottomScanEvent
import com.example.pdf_scanner.ui.component.scan.dialog.ShapeBSFragment
import com.example.pdf_scanner.ui.component.scan.dialog.TextEditorDialogFragment
import com.example.pdf_scanner.utils.FileUtil
import com.example.pdf_scanner.utils.toObject
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.oneadx.vpnclient.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author hirazy2001
 * Scan Activity to scan image
 */

@AndroidEntryPoint
class ScanActivity : BaseActivity(), ShapeBSFragment.Properties {

    lateinit var binding: ActivityScanBinding
    val viewModel: ScanViewModel by viewModels()
    lateinit var adapterImg: ImagePageAdapter
    lateinit var adapterLanguageSelected: LanguageSelectedAdapter
    lateinit var listImg: ArrayList<String>
    lateinit var listOCR: ArrayList<String>
    lateinit var adapterVpg: ImagePageAdapter
    lateinit var dataScan: DataScan
    lateinit var handler: Handler
    var optionSelected = 0
    lateinit var onEdit: onEditPhoto

    override fun initViewBinding() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.color_app)
        }

        binding = ActivityScanBinding.inflate(layoutInflater)

        dataScan = intent.getStringExtra(KEY_DATA_SCAN)!!.toObject() as DataScan

        if (dataScan.status == KEY_OCR) {
            binding.layoutScanLanguage.visibility = View.VISIBLE

            adapterLanguageSelected = LanguageSelectedAdapter(object : RecyclerItemListener {
                override fun onItemSelected(index: Int, data: OBase) {
                    var intent = Intent(this@ScanActivity, OCRActivity::class.java)
                    // intent.putExtra(KEY_DATA_OCR, DataOCR(listOCR).toJSON())
                    startActivityForResult(intent, KEY_RESULT_OCR)
                }

                override fun onOption(index: Int, data: OBase) {

                }
            })

            binding.rcclvScanLanguage.layoutManager =
                LinearLayoutManager(this@ScanActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.rcclvScanLanguage.adapter = adapterLanguageSelected

            viewModel.fetchLanguage()
            binding.layoutScanLanguage.setOnClickListener {
                var intent = Intent(this@ScanActivity, OCRActivity::class.java)
                startActivityForResult(intent, KEY_RESULT_OCR)
            }
        }

        listImg = dataScan.listImg

        // Current page 1
        binding.tvCountPage.text = "1 / " + listImg.size.toString()

        setSupportActionBar(binding.tbScan)

        binding.tbScan.setNavigationIcon(R.drawable.ic_back)

        binding.tbScan.setNavigationOnClickListener {
            var dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
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

        adapterImg = ImagePageAdapter(supportFragmentManager, listImg)

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

        binding.animScan.setAnimation(R.raw.scanner)
        binding.animScan.playAnimation()
        binding.animScan.repeatCount = 1

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
                    // adapterImg.getItem(binding.vpgImg.currentItem).
                    onEdit.addText(inputText!!, styleBuilder)
                }
            })

        }

        binding.layoutFilter.setOnClickListener {
            var intent = Intent(this@ScanActivity, FilterActivity::class.java)
            // intent.putExtra(KEY_DATA_FILTER, )
            startActivity(intent)
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
            var pos = binding.vpgImg.currentItem - 1
            if (pos >= 1) {
                binding.vpgImg.setCurrentItem(pos , true)
                var page = pos + 1
                binding.tvCountPage.text = page.toString() + " / " + listImg.size
            }
        }

        binding.btnPageNext.setOnClickListener {
            var pos = binding.vpgImg.currentItem + 1
            if (pos < adapterImg.count) {
                binding.vpgImg.setCurrentItem(pos, true)
                var page = pos + 1
                binding.tvCountPage.text = page.toString() + " / " + listImg.size
            }
        }

        binding.vpgImg.offscreenPageLimit = listImg.size

        binding.vpgImg.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                var page = position + 1
                binding.tvCountPage.text = page.toString() + " / " + listImg.size
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        adapterImg = ImagePageAdapter(supportFragmentManager, listImg)

        binding.vpgImg.adapter = adapterImg

        setContentView(binding.root)
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

    private fun u(imageBitmap: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(imageBitmap)

        val languageIdentifier = FirebaseNaturalLanguage.getInstance()
            .languageIdentification

        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().cloudTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText?> { firebaseVisionText ->
                processTxt(firebaseVisionText)
            }).addOnFailureListener(OnFailureListener { // handling an error listener.
                Toast.makeText(
                    this@ScanActivity,
                    "Fail to detect the text from image..",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun processTxt(text: FirebaseVisionText) {
        val blocks: List<FirebaseVisionText.TextBlock> = text.textBlocks

        if (blocks.isEmpty()) {
            // Toast.makeText(this@MainActivity, "No Text ", Toast.LENGTH_LONG).show()
            return
        }
        for (block in blocks) {
            val txt: String = block.text
            // textview.setText(txt)
        }
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
        observe(viewModel.listLanguage, ::handleLanguageOCR)
    }

    private fun handleLanguageOCR(list: Resource<ArrayList<String>>) {
        when (list) {
            is Resource.Success -> {
                var it = list.data
                var listLanguage = ArrayList<DataLanguage>()
                for (i in 0 until it!!.size)
                    listLanguage.add(DataLanguage(it[i]))
                adapterLanguageSelected.setData(listLanguage)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (dataScan.status == KEY_OCR) {
            menuInflater.inflate(R.menu.item_action_ocr, menu)
        } else {
            menuInflater.inflate(R.menu.item_action_scan, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionSave -> {
                var dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.dialog_save)

                var animSave = dialog.findViewById<LottieAnimationView>(R.id.animSave)
                animSave.setAnimation(R.raw.save_file)
                animSave.playAnimation()

                animSave.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        dialog.setCancelable(true)
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }
                })

                var date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val strTime = "Scan " + date.format(Date())

                var layoutName = dialog.findViewById<LinearLayout>(R.id.layoutSaveName)
                layoutName.setOnClickListener {
                    var dialogName = Dialog(this)
                    dialogName.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialogName.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialogName.setCancelable(false)
                    dialogName.setContentView(R.layout.dialog_rename)

                    var textName = dialogName.findViewById<EditText>(R.id.edtReName)
                    textName.setText(strTime)

                    var deleteName = dialogName.findViewById<ImageButton>(R.id.btnDeleteName)
                    deleteName.setOnClickListener {
                        textName.setText("")
                    }

                    var cancelBtn = dialogName.findViewById<Button>(R.id.btnDeclineReName)
                    cancelBtn.setOnClickListener {
                        dialogName.dismiss()
                    }

                    var acceptBtn = dialogName.findViewById<Button>(R.id.btnAcceptRename)
                    acceptBtn.setOnClickListener {

                    }

                    dialogName.show()
                }

                var layoutShare = dialog.findViewById<LinearLayout>(R.id.layoutSaveShare)
                layoutShare.setOnClickListener {

                }

                var layoutPrint = dialog.findViewById<LinearLayout>(R.id.layoutSavePrint)
                layoutPrint.setOnClickListener {

                }

                var layoutToAlbum = dialog.findViewById<LinearLayout>(R.id.layoutSaveToAlbum)
                layoutToAlbum.setOnClickListener {

                }

                var layoutEmail = dialog.findViewById<LinearLayout>(R.id.layoutSaveEmail)
                layoutEmail.setOnClickListener {

                }

                dialog.show()
            }

            R.id.actionUndo -> {
                // adapterImg.unDo()
            }

            R.id.actionRedo -> {
                //adapterImg.reDo()
            }

            R.id.actionOCR -> {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == KEY_RESULT_OCR) {
            // viewModel.fetchLanguage()

            // Reset OCR Language
            if (dataScan.status == KEY_OCR) {
                viewModel.fetchLanguage()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
            args.putInt(NUMBER, number)
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
            mNum = if (arguments != null) requireArguments().getInt(NUMBER) else 1
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val root: View = inflater.inflate(R.layout.item_page_image, container, false)
            val number = requireArguments().getInt(NUMBER)
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
                    if (mNum == requireArguments().getInt(NUMBER)) {
                        mPhotoEditor!!.addText(inputText, styleBuilder)
                    }
                }

                override fun save() {

                }
            })

            return root
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            listAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_list_item_1, list
            )
        }

        fun addText(pos: Int, inputText: String, styleBuilder: TextStyleBuilder) {
            if (pos == requireArguments().getInt("num")) {
                Log.e("addText", "")
                mPhotoEditor!!.addText(inputText, styleBuilder)
            }
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
            mPhotoEditor.addText(inputText, styleBuilder)
        }

        override fun save() {

        }
    }

    class ImageScanFragment(var path: String) : Fragment() {

        lateinit var mPhotoEditor: PhotoEditor
        lateinit var mPhotoEditorView: PhotoEditorView
        lateinit var mShapeBuilder: ShapeBuilder

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val root: View = inflater.inflate(R.layout.item_page_image, container, false)
            val number = requireArguments().getInt(NUMBER)
            mPhotoEditorView = root!!.findViewById(R.id.photoEditor)

            var file = File(path)

            if (file.exists()) {
                val uri: Uri = Uri.fromFile(file)
                mPhotoEditorView!!.source.setImageURI(uri)
            }

            mPhotoEditor = PhotoEditor.Builder(context, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build()
            // mPhotoEditor!!.setOnPhotoEditorListener(this)
            mPhotoEditor!!.setFilterEffect(PhotoFilter.BLACK_WHITE)
            mShapeBuilder = ShapeBuilder()
            mPhotoEditor!!.setShape(mShapeBuilder)


            var act = activity as ScanActivity
            act.setOnEdit(object : onEditPhoto {
                override fun addText(inputText: String, styleBuilder: TextStyleBuilder) {

                }

                override fun save() {

                }
            })

            //!!.addView(root)

            return root
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

                }

                override fun onFailure(exception: Exception) {

                }
            })
        }

        fun addText(pos: Int, inputText: String, styleBuilder: TextStyleBuilder) {
            if (pos == requireArguments().getInt("num")) {
                Log.e("addText", "")
                mPhotoEditor!!.addText(inputText, styleBuilder)
            }
        }
    }


    class ImagePageAdapter(fm: FragmentManager, var list: ArrayList<String>) :
        FragmentStatePagerAdapter(fm) {

        var position = 0
        var mNum: Int = 0

        override fun getCount(): Int {
            return list.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            return super.instantiateItem(container, position)
        }

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            try {
                fragment = ImageFragment(list).newInstance(position)
            } catch (e: Exception) {
            }
            return fragment!!
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            super.destroyItem(container, position, `object`)
        }

        fun canDelete(): Boolean {
            return list.size > 0
        }
    }

}