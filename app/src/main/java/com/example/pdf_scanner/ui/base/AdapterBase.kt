package com.example.pdf_scanner.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.pdf_scanner.data.dto.OBase
import com.example.pdf_scanner.ui.base.listener.RecycleFolderListener
import com.example.pdf_scanner.utils.LayoutId
import com.example.pdf_scanner.ui.base.listener.RecyclerItemListener

class BaseHolder<T : ViewDataBinding>(var itemBinding: T) :
    RecyclerView.ViewHolder(itemBinding.root)

abstract class AdapterBase<K : OBase, T : ViewDataBinding>(var event: RecyclerItemListener) :
    RecyclerView.Adapter<BaseHolder<T>>() {

    protected var listData = arrayListOf<K>()


    fun setData(it: List<K>) {
        listData.clear()
        listData = it as ArrayList<K>
        notifyDataSetChanged()
    }

    fun setValue(it: ArrayList<K>){
        listData = it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<T> {
        return BaseHolder(
            DataBindingUtil.inflate<T>
                (
                LayoutInflater.from(parent.context),
                javaClass.getAnnotation(LayoutId::class.java).value,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: BaseHolder<T>, position: Int) {
        bindView(holder, position)

        holder.itemBinding.root.setOnClickListener {
            event?.onItemSelected(position, listData[position])
        }

        holder.itemBinding.root.setOnLongClickListener() {
            event?.onItemSelected(position, listData[position])
            true
        }
    }

    abstract fun bindView(itemBinding: BaseHolder<T>, position: Int)

}


abstract class AdapterFolder<K : OBase, T : ViewDataBinding>(var event: RecycleFolderListener) :
    RecyclerView.Adapter<BaseHolder<T>>() {

    protected var listData = arrayListOf<K>()


    fun setData(it: List<K>) {
        listData.clear()
        listData = it as ArrayList<K>
        notifyDataSetChanged()
    }

    fun setValue(it: ArrayList<K>){
        listData = ArrayList(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<T> {
        return BaseHolder(
            DataBindingUtil.inflate<T>
                (
                LayoutInflater.from(parent.context),
                javaClass.getAnnotation(LayoutId::class.java).value,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: BaseHolder<T>, position: Int) {
        bindView(holder, position)

        holder.itemBinding.root.setOnClickListener {
            event?.onItemSelected(position, listData[position])
        }
    }

    abstract fun bindView(itemBinding: BaseHolder<T>, position: Int)

}