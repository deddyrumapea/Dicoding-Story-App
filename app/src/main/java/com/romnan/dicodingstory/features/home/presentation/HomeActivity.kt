package com.romnan.dicodingstory.features.home.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.home.presentation.adapter.StoryAdapter
import com.romnan.dicodingstory.features.login.presentation.LoginActivity
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
        val pbStoriesList = findViewById<ProgressBar>(R.id.pb_stories_list)

        val storyAdapter = StoryAdapter()
        rvStoriesList.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter
        }

        viewModel.storiesList.observe(this) { storiesList ->
            storyAdapter.setStoriesList(storiesList)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            pbStoriesList.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvStoriesList.visibility = if (!isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { uiText ->
            val message = when (uiText) {
                is UIText.DynamicString -> uiText.value
                is UIText.StringResource -> getString(uiText.id)
            }

            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}