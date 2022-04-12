package com.romnan.dicodingstory.features.home.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.Story

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val storiesList = ArrayList<Story>()
    var onItemClick: ((View, Story) -> Unit)? = null

    fun setStoriesList(newStoriesList: List<Story>) {
        val diffResult = DiffUtil.calculateDiff(
            StoryItemDiffCallback(oldItems = storiesList, newItems = newStoriesList)
        )
        storiesList.clear()
        storiesList.addAll(newStoriesList)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class StoryItemDiffCallback(
        val oldItems: List<Story>,
        val newItems: List<Story>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].id == newItems[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]

    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.rootView.setOnClickListener {
                onItemClick?.invoke(it, storiesList[adapterPosition])
            }
        }

        fun bind(story: Story) {
            val ivPhoto = itemView.findViewById<ImageView>(R.id.iv_story_item_photo)
            val tvUserName = itemView.findViewById<TextView>(R.id.tv_story_item_user_name)

            Glide.with(itemView)
                .load(story.photoUrl)
                .into(ivPhoto)
            tvUserName.text = story.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_story, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storiesList[position])
    }

    override fun getItemCount(): Int = storiesList.size
}