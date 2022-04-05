package com.romnan.dicodingstory.features.home.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.romnan.dicodingstory.R
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
    }
}