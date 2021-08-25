package com.example.pdf_scanner.ui.component.scan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pdf_scanner.R


class ImageFragment(var list: ArrayList<String>): Fragment() {

    fun newInstance(number: Int): ImageFragment? {
        val args = Bundle()
        args.putInt("number", number)
        val fragment = ImageFragment(list)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.item_page_image, container, false)
        val number = requireArguments().getInt("number")


        return root
    }
}