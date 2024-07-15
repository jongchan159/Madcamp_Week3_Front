package com.example.project3.Store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.R

class StoreAdapter (private var items: List<String>) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.text_name)
        val textPrice: TextView = view.findViewById(R.id.text_price)
        val storeImage: ImageView = view.findViewById(R.id.store_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storeItems = items[position]
        holder.textName.text = storeItems[position].toString()
        holder.textPrice.text = storeItems[position].toString()
        // holder.storeImage. 이미지
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}