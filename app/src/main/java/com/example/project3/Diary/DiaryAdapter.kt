// DiaryAdapter.kt
package com.example.project3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryAdapter(private val diaries: List<Diary>, private val onItemClick: (Diary) -> Unit) : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    class ViewHolder(view: View, onItemClick: (Diary) -> Unit, diaries: List<Diary>) : RecyclerView.ViewHolder(view) {
        val contents: TextView = view.findViewById(R.id.diary_contents)

        init {
            view.setOnClickListener {
                onItemClick(diaries[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_list, parent, false)
        return ViewHolder(view, onItemClick, diaries)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary = diaries[position]
        holder.contents.text = diary.contents
    }

    override fun getItemCount() = diaries.size
}
