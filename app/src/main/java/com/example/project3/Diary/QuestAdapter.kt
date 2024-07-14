package com.example.project3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestAdapter(private val quests: List<Quest>) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contents: TextView = view.findViewById(R.id.quest_contents)
        val isComplete: TextView = view.findViewById(R.id.quest_is_complete)
        val type: TextView = view.findViewById(R.id.quest_type)
        val createdAt: TextView = view.findViewById(R.id.quest_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quest_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quest = quests[position]
        holder.contents.text = quest.contents
        holder.isComplete.text = if (quest.isComplete) "완료" else "미완료"
        holder.type.text = quest.type
        holder.createdAt.text = quest.createdAt
    }

    override fun getItemCount() = quests.size
}
