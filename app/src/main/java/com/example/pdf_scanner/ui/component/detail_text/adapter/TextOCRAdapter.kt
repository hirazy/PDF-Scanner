package com.example.pdf_scanner.ui.component.detail_text.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pdf_scanner.ui.component.detail_text.fragment.ImageDetailFragment

class ViewPagerMainAdapter(
    fm: FragmentActivity
) : FragmentStateAdapter(fm) {

    private val arrayList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun addFragment(fragment: Fragment) {
        arrayList.add(fragment)
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ImageDetailFragment()
            //  1 -> return OCRTextFragment(this)
        }
        return Fragment()
    }
}