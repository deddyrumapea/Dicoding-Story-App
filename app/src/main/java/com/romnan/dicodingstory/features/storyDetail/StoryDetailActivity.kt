package com.romnan.dicodingstory.features.storyDetail

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.presentation.model.StoryParcelable

class StoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.story_detail)
        setContentView(R.layout.activity_story_detail)

        val story = intent.getParcelableExtra<StoryParcelable>(EXTRA_STORY_PARCELABLE)

        val tvUserName = findViewById<TextView>(R.id.tv_detail_user_name)
        val tvDescription = findViewById<TextView>(R.id.tv_detail_description)
        val ivPhoto = findViewById<ImageView>(R.id.iv_detail_photo)

        story?.name?.let { tvUserName.text = it }
        story?.description?.let { tvDescription.text = it }
        story?.photoUrl?.let {
            Glide.with(this)
                .load(it)
                .into(ivPhoto)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    companion object {
        const val EXTRA_STORY_PARCELABLE = "extra_story_parcelable"
    }
}