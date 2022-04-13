package com.romnan.dicodingstory.features.home.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romnan.dicodingstory.R

class StoriesLoadStateAdapter : LoadStateAdapter<StoriesLoadStateAdapter.ViewHolder>() {
    var onRetry: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(
            itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_loading, parent, false),
            retry = onRetry
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class ViewHolder(
        itemView: View,
        retry: (() -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {
        private val btnRetry = itemView.findViewById<Button>(R.id.btn_item_loading_retry)
        private val tvErrorMsg = itemView.findViewById<TextView>(R.id.tv_item_loading_error_msg)
        private val pbLoading = itemView.findViewById<ProgressBar>(R.id.pb_item_loading)

        init {
            btnRetry.setOnClickListener { retry?.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                tvErrorMsg.text = loadState.error.localizedMessage
            }

            pbLoading.isVisible = loadState is LoadState.Loading
            btnRetry.isVisible = loadState is LoadState.Error
            tvErrorMsg.isVisible = loadState is LoadState.Error
        }

    }
}