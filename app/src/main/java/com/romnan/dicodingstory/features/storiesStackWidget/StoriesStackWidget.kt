package com.romnan.dicodingstory.features.storiesStackWidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.CoreRepository
import com.romnan.dicodingstory.core.layers.presentation.model.StoryParcelable
import com.romnan.dicodingstory.core.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class StoriesStackWidget : AppWidgetProvider() {

    @Inject
    lateinit var coreRepo: CoreRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            getAllStories(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action != null && intent.action == TOAST_ACTION) {
            val name = intent.getStringExtra(EXTRA_ITEM)
            Toast.makeText(context, "Touched $name's story", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, StoriesStackWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = this.toUri(Intent.URI_INTENT_SCHEME).toUri()
        }

        val remoteViews = RemoteViews(context.packageName, R.layout.stories_stack_widget)
        remoteViews.setRemoteAdapter(R.id.sv_stories, intent)
        remoteViews.setEmptyView(R.id.sv_stories, R.id.tv_no_data)

        val toastIntent = Intent(context, StoriesStackWidget::class.java).apply {
            action = TOAST_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        else 0

        val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, flag)
        remoteViews.setPendingIntentTemplate(R.id.sv_stories, toastPendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    private val supervisorJob = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private var getAllStoriesJob: Job? = null

    private fun getAllStories(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        getAllStoriesJob?.cancel()
        getAllStoriesJob = coroutineScope.launch {
            coreRepo.getAllStories().onEach { result ->
                if (result is Resource.Success) {
                    val stories = result.data ?: emptyList()
                    val storiesArrayList = ArrayList(stories.map { StoryParcelable(it) })

                    Intent(StoriesStackRemoteViewsFactory.ACTION_UPDATE_STORIES).apply {
                        putParcelableArrayListExtra(
                            StoriesStackRemoteViewsFactory.EXTRA_STORIES, storiesArrayList
                        )
                    }.run {
                        context.sendBroadcast(this)
                        appWidgetManager.notifyAppWidgetViewDataChanged(
                            appWidgetId,
                            R.id.sv_stories
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    companion object {
        private const val TOAST_ACTION = "com.romnan.dicodingstory.TOAST_ACTION"
        const val EXTRA_ITEM = "com.romnan.dicodingstory.EXTRA_ITEM"
    }
}