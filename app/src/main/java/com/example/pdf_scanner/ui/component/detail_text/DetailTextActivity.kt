package com.example.pdf_scanner.ui.component.detail_text

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.pdf_scanner.IMAGE
import com.example.pdf_scanner.KEY_DATA_DETAIL_TEXT
import com.example.pdf_scanner.R
import com.example.pdf_scanner.TEXT
import com.example.pdf_scanner.data.dto.DataDetailText
import com.example.pdf_scanner.data.dto.OCRListener
import com.example.pdf_scanner.databinding.ActivityDetailTextBinding
import com.example.pdf_scanner.ui.base.BaseActivity
import com.example.pdf_scanner.ui.component.detail_text.fragment.*
import com.example.pdf_scanner.ui.component.scan.dialog.ShapeBSFragment
import com.example.pdf_scanner.utils.toObject
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import dagger.hilt.android.AndroidEntryPoint
import ja.burhanrashid52.photoeditor.shape.ShapeType
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import java.io.File

@AndroidEntryPoint
class DetailTextActivity : BaseActivity(), ShapeBSFragment.Properties {

    lateinit var binding: ActivityDetailTextBinding
    lateinit var data: DataDetailText
    lateinit var adapter: PagerAdapter
    var textOCR: String = ""

    override fun initViewBinding() {
        binding = ActivityDetailTextBinding.inflate(layoutInflater)

        data = intent.getStringExtra(KEY_DATA_DETAIL_TEXT)!!.toObject()

        setSupportActionBar(binding.tbDetailText)
        binding.tbDetailText.setNavigationOnClickListener {
            back()
        }
        adapter =
            PagerAdapter(supportFragmentManager, data.filePath, object : OnTextListener {
                override fun onDelete() {
                    var file = File(data.filePath)
                    file.delete()
                    setResult(RESULT_OK)
                    finish()
                }

                override fun onOCR(text: String) {
                    textOCR = text
                }

                override fun onSave() {
                    finish()
                }

                override fun onSign() {
                    var dialog = ShapeBSFragment()
                    showBottomSheetDialogFragment(dialog)
                    dialog.setPropertiesChangeListener(this@DetailTextActivity)
                }
            })
        binding.vpgDetailText.adapter = adapter
        binding.vpgDetailText.offscreenPageLimit = 2
        initTab()
        setContentView(binding.root)
    }

    private fun back() {
        var dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_leave_scan)

        var btnCancel = dialog.findViewById<Button>(R.id.btnDecline)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        var txtContent = dialog.findViewById<TextView>(R.id.tvContentLeaveScan)
        var textContent = "Discard unsaved changes"
        txtContent.text = textContent

        var tvContentLeave = dialog.findViewById<TextView>(R.id.tvContentLeave)
        var txtDiscard = "Tips"
        tvContentLeave.text = txtDiscard

        var btnLeave = dialog.findViewById<Button>(R.id.btnAcceptLeave)
        var txtLeave = "Discard"
        btnLeave.text = txtLeave
        btnLeave.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun initTab() {
        binding.tabDetailText.setBackgroundResource(R.drawable.round_indicator_bg)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return 2
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val clipPagerTitleView = ClipPagerTitleView(context)
                if (index == 0) {
                    clipPagerTitleView.text = IMAGE
                } else {
                    clipPagerTitleView.text = TEXT
                }
                clipPagerTitleView.textColor = Color.parseColor("#e94220")
                clipPagerTitleView.clipColor = Color.WHITE
                clipPagerTitleView.setOnClickListener { binding.vpgDetailText.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                val navigatorHeight =
                    context.resources.getDimension(R.dimen.common_navigator_height)
                val borderWidth = UIUtil.dip2px(context, 1.0).toFloat()
                val lineHeight = navigatorHeight - 2 * borderWidth
                indicator.lineHeight = lineHeight
                indicator.roundRadius = lineHeight / 2
                indicator.yOffset = borderWidth
                indicator.setColors(Color.parseColor("#bc2a2a"))
                return indicator
            }
        }
        binding.tabDetailText.navigator = commonNavigator
        ViewPagerHelper.bind(binding.tabDetailText, binding.vpgDetailText)
    }

    override fun observeViewModel() {

    }

    private fun genPath(): String {
        var rootPath = data.filePath
        var path = ""
        var pathName = ""
        for (i in rootPath.indices.reversed()) {
            if (rootPath[i] == '/') {
                pathName = rootPath.substring(i + 1, rootPath.length - 4)
                rootPath = rootPath.substring(0, i + 1)
                break
            }
        }
        path = rootPath + pathName
        return path
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_detail_text, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemActionSaveDetail -> {
                if (textOCR.isNotEmpty()) {

                    var path = genPath()
                    Log.e("onOptionsItemSelected", path)
                    var file = File("$path.txt")
                    file.writeText(textOCR)
                    var isSuccess = file.createNewFile()
                    if (file.exists()) {
                        DynamicToast.makeSuccess(
                            this@DetailTextActivity,
                            "Create file successfully!"
                        ).show()
                    } else {
                        DynamicToast.makeError(this@DetailTextActivity, "Create file error!").show()
                    }
                }
                adapter.save()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class PagerAdapter(var frag: FragmentManager, var path: String, var e: OnTextListener) :
        FragmentStatePagerAdapter(frag) {

        var list: ArrayList<Fragment> = ArrayList()

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            if (position == 0 && list.size == 0) {
                var fragmentImg = ImageDetailFragment(path, object : OnImageTextListener {
                    override fun onPrint() {

                    }

                    override fun toAlbum() {

                    }

                    override fun onDelete() {
                        e.onDelete()
                    }

                    override fun onSign() {
                        e.onSign()
                    }

                    override fun onSave() {
                        e.onSave()
                    }
                })
                var fragmentOCR = OCRTextFragment(path, object : OCRListener {
                    override fun detectOCR(text: String) {
                        e.onOCR(text)
                    }
                })
                list.add(fragmentImg)
                list.add(fragmentOCR)
                return fragmentImg
            }
            return list[position]
        }

        private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
            if (fragment == null || fragment.isAdded) {
                return
            }
            fragment.show(frag, fragment.tag)
        }

        fun save() {
            var imgFragment = list[0] as ImageDetailFragment
            imgFragment.save()
        }

        fun changeColor(colorCode: Int) {
            var imgFragment = list[0] as ImageDetailFragment
            imgFragment.setOnCoLorChanged(colorCode)
        }

        fun changeOpacity(opacity: Int) {
            var imgFragment = list[0] as ImageDetailFragment
            imgFragment.setOnOpacityChanged(opacity)
        }

        fun changeShapeSized(shapeSize: Int) {
            var imgFragment = list[0] as ImageDetailFragment
            imgFragment.setOnShapeSizeChanged(shapeSize)
        }

        fun changeShaped(shapeType: ShapeType?) {
            var imgFragment = list[0] as ImageDetailFragment
            imgFragment.setShape(shapeType)
        }

    }

    override fun onColorChanged(colorCode: Int) {
        adapter.changeColor(colorCode)
    }

    override fun onOpacityChanged(opacity: Int) {
        adapter.changeOpacity(opacity)
    }

    override fun onShapeSizeChanged(shapeSize: Int) {
        adapter.changeShapeSized(shapeSize)
    }

    override fun onShapePicked(shapeType: ShapeType?) {
        adapter.changeShaped(shapeType)
    }
}