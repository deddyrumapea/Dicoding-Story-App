package com.romnan.dicodingstory.features.home.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.Story

class StoriesPagingAdapter :
    PagingDataAdapter<Story, StoriesPagingAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((View, Story) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_story, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.rootView.setOnClickListener { itemRootView ->
                val story = getItem(bindingAdapterPosition)
                story?.let { onItemClick?.invoke(itemRootView, it) }
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

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}