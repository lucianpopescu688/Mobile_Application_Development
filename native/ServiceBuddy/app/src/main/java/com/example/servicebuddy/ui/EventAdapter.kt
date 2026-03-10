package com.example.servicebuddy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.servicebuddy.R
import com.example.servicebuddy.model.MaintenanceEvent
import java.util.Locale
import java.util.concurrent.TimeUnit

class EventAdapter(
    private val onClick: (MaintenanceEvent) -> Unit
) : ListAdapter<MaintenanceEvent, EventAdapter.EventViewHolder>(EventDiffCallback) {

    class EventViewHolder(itemView: View, val onClick: (MaintenanceEvent) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val vehicleTextView: TextView = itemView.findViewById(R.id.tvVehicleIdentifier)
        private val idTextView: TextView = itemView.findViewById(R.id.tvEventId)
        private val statusTextView: TextView = itemView.findViewById(R.id.tvStatus)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDate)
        private val priceTextView: TextView = itemView.findViewById(R.id.tvPrice)
        private val statusBar: View = itemView.findViewById(R.id.statusBar)
        private val eventIcon: ImageView = itemView.findViewById(R.id.ivEventIcon)
        private var currentEvent: MaintenanceEvent? = null

        init {
            itemView.setOnClickListener { currentEvent?.let { onClick(it) } }
        }

        fun bind(event: MaintenanceEvent) {
            currentEvent = event
            titleTextView.text = event.title
            vehicleTextView.text = event.vehicleIdentifier
            idTextView.text = "ID: ${event.id}"
            statusTextView.text = event.status
            priceTextView.text = "$${event.price}"

            
            val diff = event.dueDate.time - System.currentTimeMillis()
            val days = TimeUnit.MILLISECONDS.toDays(diff)

            dateTextView.text = when {
                days < -1 -> "${-days} days ago"
                days == -1L -> "1 day ago"
                days == 0L -> "Today"
                days == 1L -> "in 1 day"
                else -> "in $days days"
            }

            
            val statusColor = when (event.status.uppercase(Locale.US)) {
                "OVERDUE" -> R.color.status_red
                "UPCOMING" -> R.color.status_orange
                "FUTURE" -> R.color.status_light_blue
                "COMPLETED" -> R.color.status_green
                else -> R.color.status_blue
            }
            statusBar.setBackgroundColor(ContextCompat.getColor(itemView.context, statusColor))
            statusTextView.setTextColor(ContextCompat.getColor(itemView.context, statusColor))

            
            val icon = when (event.category.uppercase(Locale.US)) {
                "SERVICE" -> R.drawable.ic_wrench
                "DOCUMENT" -> R.drawable.ic_document
                else -> R.drawable.ic_wrench
            }
            eventIcon.setImageResource(icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object EventDiffCallback : DiffUtil.ItemCallback<MaintenanceEvent>() {
    override fun areItemsTheSame(oldItem: MaintenanceEvent, newItem: MaintenanceEvent): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MaintenanceEvent, newItem: MaintenanceEvent): Boolean = oldItem == newItem
}
