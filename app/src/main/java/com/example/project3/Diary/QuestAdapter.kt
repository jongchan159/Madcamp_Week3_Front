// QuestAdapter.kt
package com.example.project3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestAdapter(private val quests: List<Quest>, private val onItemClick: (Quest) -> Unit) : RecyclerView.Adapter<QuestAdapter.ViewHolder>() {

    class ViewHolder(view: View, onItemClick: (Quest) -> Unit, quests: List<Quest>) : RecyclerView.ViewHolder(view) {
        val contents: TextView = view.findViewById(R.id.quest_contents)
        val isComplete: TextView = view.findViewById(R.id.quest_is_complete)
        val type: TextView = view.findViewById(R.id.quest_type)

        init {
            view.setOnClickListener {
                onItemClick(quests[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quest_list, parent, false)
        return ViewHolder(view, onItemClick, quests)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quest = quests[position]
        holder.contents.text = quest.contents
        holder.isComplete.text = if (quest.isComplete) "완료" else "미완료"
        holder.type.text = quest.type
    }

    override fun getItemCount() = quests.size
}
