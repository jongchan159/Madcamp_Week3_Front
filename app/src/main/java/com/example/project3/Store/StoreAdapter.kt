package com.example.project3.Store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project3.Class.Item
import com.example.project3.R
import pl.droidsonroids.gif.GifImageView

class StoreAdapter(
    private var items: List<Item>,
    private val purchasedItemIds: Set<Int>,
    private val listener: OnButtonClickListener
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    interface OnButtonClickListener {
        fun onButtonClick(item: Item)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.text_name)
        val textPrice: TextView = view.findViewById(R.id.text_price)
        val storeImage: GifImageView = view.findViewById(R.id.store_image)
        val button: Button = view.findViewById(R.id.button_buy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storeItem = items[position]
        holder.textName.text = storeItem.item_name
        holder.textPrice.text = storeItem.item_price.toString()
        val context = holder.itemView.context

        // Glide를 사용하여 이미지 로드
        when (storeItem.item_name) {
            "토끼" -> Glide.with(context).load(R.raw.rabbit).into(holder.storeImage)
            "버섯무리" -> Glide.with(context).load(R.raw.mushroom).into(holder.storeImage)
            "무사" -> Glide.with(context).load(R.raw.musa).into(holder.storeImage)
            "아이언맨" -> Glide.with(context).load(R.raw.ironman).into(holder.storeImage)
            else -> holder.storeImage.setImageResource(R.raw.default_char)
        }

        // 버튼 텍스트 및 상태 설정
        if (storeItem.item_id in purchasedItemIds) {
            holder.button.text = "보유중"
            holder.button.isEnabled = false
        } else {
            holder.button.text = "구매하기"
            holder.button.isEnabled = true
        }

        holder.button.setOnClickListener {
            if (storeItem.item_id !in purchasedItemIds) {
                listener.onButtonClick(storeItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }
}