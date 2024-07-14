package com.example.project3.Diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project3.R

//class DiaryAdapter(private val diaryEntries: List<String>) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
//
//    class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val diaryEntryTextView: TextView = itemView.findViewById(R.id.diaryEntryTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.diary_entry_item, parent, false)
//        return DiaryViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
//        holder.diaryEntryTextView.text = diaryEntries[position]
//    }
//
//    override fun getItemCount() = diaryEntries.size
//}