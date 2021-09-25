package com.example.pdf_scanner.ui.component.main.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.pdf_scanner.data.dto.OptionCamera

class OptionAdapter(private val mDataList: List<OptionCamera>?) :
    PagerAdapter() {

    override fun getCount(): Int {
        return mDataList?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val textView = TextView(container.context)
        textView.text = mDataList!![position].name
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.textSize = 24f
        container.addView(textView)
        return textView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        val textView = `object` as TextView
        val text = textView.text.toString()
        for(i in mDataList!!.indices){
            if(mDataList[i].name == text)
                return i
        }
        return POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mDataList!![position].name
    }
}