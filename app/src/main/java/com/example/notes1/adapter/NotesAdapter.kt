package com.example.notes1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes1.R
import com.example.notes1.model.Note

class NotesAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Boolean
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardNote)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNoteTitle)
        private val tvPreview: TextView = itemView.findViewById(R.id.tvNotePreview)
        private val tvDate: TextView = itemView.findViewById(R.id.tvNoteDate)
        private val tvIndex: TextView = itemView.findViewById(R.id.tvNoteIndex)

        private val cardColors = listOf(
            0xFFFFF9C4.toInt(), // Yellow
            0xFFE8F5E9.toInt(), // Green
            0xFFE3F2FD.toInt(), // Blue
            0xFFFCE4EC.toInt(), // Pink
            0xFFF3E5F5.toInt(), // Purple
            0xFFE0F7FA.toInt(), // Cyan
            0xFFFFF3E0.toInt()  // Orange
        )

        fun bind(note: Note) {
            val position = bindingAdapterPosition

            tvTitle.text = note.title
            tvPreview.text = note.getPreview()
            tvDate.text = note.getFormattedDate()
            tvIndex.text = itemView.context.getString(R.string.note_index, position + 1)

            val colorIndex = (position % cardColors.size)
            cardView.setCardBackgroundColor(cardColors[colorIndex])

            itemView.setOnClickListener { onNoteClick(note) }
            itemView.setOnLongClickListener { onNoteLongClick(note) }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
    }
}
