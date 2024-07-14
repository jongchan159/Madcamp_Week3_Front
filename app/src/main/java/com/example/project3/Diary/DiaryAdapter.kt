package com.example.project3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryAdapter(private val diaries: List<Diary>) : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contents: TextView = view.findViewById(R.id.diary_contents)
        val createdAt: TextView = view.findViewById(R.id.diary_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = diaries[position]
        holder.contents.text = diary.contents
        holder.createdAt.text = diary.createdAt
    }

    override fun getItemCount() = diaries.size
}
