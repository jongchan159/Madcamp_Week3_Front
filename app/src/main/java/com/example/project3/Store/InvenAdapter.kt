package com.example.project3.Store

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project3.Class.Item
import com.example.project3.MainActivity
import com.example.project3.R
import com.example.project3.UserHolder
import pl.droidsonroids.gif.GifImageView

class InvenAdapter(
    private var items: List<Item>,
    private var equippedItemId: Int?,
    private val context: Context
) : RecyclerView.Adapter<InvenAdapter.ViewHolder>() {

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
        val invenItem = items[position]
        holder.textName.text = invenItem.item_name
        holder.textPrice.text = invenItem.item_price.toString()
        val context = holder.itemView.context

        // Glide를 사용하여 이미지 로드
        when (invenItem.item_name) {
            "토끼" -> Glide.with(context).load(R.raw.rabbit).into(holder.storeImage)
            "버섯무리" -> Glide.with(context).load(R.raw.mushroom).into(holder.storeImage)
            "무사" -> Glide.with(context).load(R.raw.musa).into(holder.storeImage)
            "아이언맨" -> Glide.with(context).load(R.raw.ironman).into(holder.storeImage)
            else -> holder.storeImage.setImageResource(R.raw.default_char)
        }

        // 버튼 텍스트 및 상태 설정
        if (invenItem.item_id == equippedItemId) {
            holder.button.text = "착용중"
        } else {
            holder.button.text = "착용하기"
        }

        holder.button.setOnClickListener {
            // 유저의 캐릭터아이디를 업데이트
            val user = UserHolder.getUser()
            user?.let {
                it.characterId = invenItem.item_name
                UserHolder.setUser(it)

                // MainActivity로 돌아가기
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
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

    fun updateEquippedItem(equippedItemId: Int?) {
        this.equippedItemId = equippedItemId
        notifyDataSetChanged()
    }
}
