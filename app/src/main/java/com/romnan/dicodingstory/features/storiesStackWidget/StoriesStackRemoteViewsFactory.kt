package com.romnan.dicodingstory.features.storiesStackWidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.presentation.model.StoryParcelable

internal class StoriesStackRemoteViewsFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory, BroadcastReceiver() {

    private var storiesList = emptyList<Story>()

    private val updateStoriesIntentFilter = IntentFilter().apply {
        addAction(ACTION_UPDATE_STORIES)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val storyParcelables =
            intent?.getParcelableArrayListExtra<StoryParcelable>(EXTRA_STORIES) ?: emptyList()
        storiesList = storyParcelables.map { it.toStory() }
        onDataSetChanged()
    }

    override fun onCreate() {
        context.registerReceiver(this, updateStoriesIntentFilter)
    }

    override fun onDestroy() {
        context.unregisterReceiver(this)
    }

    override fun onDataSetChanged() {}

    override fun getCount(): Int = storiesList.size

    override fun getViewAt(position: Int): RemoteViews {
        val story = storiesList[position]

        val remoteViews = RemoteViews(context.packageName, R.layout.item_story_stack)

        remoteViews.setTextViewText(R.id.tv_item_story_stack_name, story.name)

        val storyPhotoBitmap = Glide
            .with(context)
            .asBitmap()
            .apply(RequestOptions().override(200, 200))
            .load(story.photoUrl)
            .submit()
            .get()

        remoteViews.setImageViewBitmap(R.id.iv_item_story_stack_photo, storyPhotoBitmap)

        val extras = bundleOf(StoriesStackWidget.EXTRA_ITEM to story.name)

        remoteViews.setOnClickFillInIntent(
            R.id.iv_item_story_stack_photo,
            Intent().apply { putExtras(extras) }
        )

        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    companion object {
        const val ACTION_UPDATE_STORIES = "action_update_stories"
        const val EXTRA_STORIES = "extra_stories"
    }
}