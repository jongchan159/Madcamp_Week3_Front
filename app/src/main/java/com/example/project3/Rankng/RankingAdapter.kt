package com.example.project3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(
    private val userList: List<User>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.RankingList_name_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ranking_list, parent, false)
        return RankingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userNameTextView.text = currentUser.userName
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(currentUser)
        }
    }

    override fun getItemCount() = userList.size
}
