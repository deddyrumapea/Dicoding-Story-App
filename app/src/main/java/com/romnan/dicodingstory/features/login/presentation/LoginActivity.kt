package com.romnan.dicodingstory.features.login.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.home.presentation.HomeActivity
import com.romnan.dicodingstory.features.register.presentation.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.login)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnRegister = findViewById<Button>(R.id.btn_go_to_register)

        viewModel.isLoading.observe(this) { isLoading ->
            btnLogin.isEnabled = !isLoading
            btnLogin.text =
                if (isLoading) getString(R.string.logging_in) else getString(R.string.login)
        }

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                Intent(this@LoginActivity, HomeActivity::class.java).run {
                    startActivity(this)
                    this@LoginActivity.finish()
                }
            }
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

        btnLogin.setOnClickListener {
            viewModel.onEvent(
                LoginEvent.SendLoginRequest(
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString()
                )
            )
        }

        btnRegister.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).run {
                startActivity(this)
                finish()
            }
        }
    }
}