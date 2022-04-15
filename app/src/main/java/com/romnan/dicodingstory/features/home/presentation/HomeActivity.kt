package com.romnan.dicodingstory.features.home.presentation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.presentation.model.StoryParcelable
import com.romnan.dicodingstory.features.addStory.presentation.AddStoryActivity
import com.romnan.dicodingstory.features.home.presentation.adapter.StoriesLoadStateAdapter
import com.romnan.dicodingstory.features.home.presentation.adapter.StoriesPagingAdapter
import com.romnan.dicodingstory.features.home.presentation.model.HomeEvent
import com.romnan.dicodingstory.features.login.presentation.LoginActivity
import com.romnan.dicodingstory.features.preferences.PreferencesActivity
import com.romnan.dicodingstory.features.storiesMap.StoriesMapActivity
import com.romnan.dicodingstory.features.storyDetail.StoryDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                Intent(this@HomeActivity, LoginActivity::class.java).run {
                    startActivity(this)
                    this@HomeActivity.finish()
                }
            }
        }

        setContentView(R.layout.activity_home)

        val rvStoriesList = findViewById<RecyclerView>(R.id.rv_stories_list)
        val fabAddStory = findViewById<FloatingActionButton>(R.id.fab_add_story)

        val pagingAdapter = StoriesPagingAdapter()
        val loadStateAdapter = StoriesLoadStateAdapter().apply {
            onRetry = { pagingAdapter.retry() }
        }

        rvStoriesList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = pagingAdapter.withLoadStateFooter(loadStateAdapter)
        }

        pagingAdapter.onItemClick = { itemView: View, story: Story ->
            val ivPhoto = itemView.findViewById<ImageView>(R.id.iv_story_item_photo)
            val tvName = itemView.findViewById<TextView>(R.id.tv_story_item_user_name)

            val animationBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@HomeActivity,
                Pair(ivPhoto, getString(R.string.tn_iv_detail_photo)),
                Pair(tvName, getString(R.string.tn_tv_detail_user_name))
            ).toBundle()

            Intent(this, StoryDetailActivity::class.java).apply {
                putExtra(StoryDetailActivity.EXTRA_STORY_PARCELABLE, StoryParcelable(story))
                startActivity(this, animationBundle)
            }
        }

        fabAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        viewModel.storiesList.observe(this) { storiesList ->
            pagingAdapter.submitData(lifecycle, storiesList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_stories_map -> startActivity(Intent(this, StoriesMapActivity::class.java))
            R.id.mi_logout -> viewModel.onEvent(HomeEvent.Logout)
            R.id.mi_preferences -> startActivity(Intent(this, PreferencesActivity::class.java))
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
}
