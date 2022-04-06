package com.romnan.dicodingstory.features.register.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.login.presentation.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.register)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.et_register_name)
        val etEmail = findViewById<EditText>(R.id.et_register_email)
        val etPassword = findViewById<EditText>(R.id.et_register_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val btnLogin = findViewById<Button>(R.id.btn_go_to_login)

        viewModel.isLoading.observe(this) { isLoading ->
            etName.isEnabled = !isLoading
            etEmail.isEnabled = !isLoading
            etPassword.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
            btnRegister.text =
                if (isLoading) getString(R.string.registering) else getString(R.string.register)
        }

        viewModel.isRegistered.observe(this) { isRegistered ->
            if (isRegistered) {
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    putExtra(LoginActivity.EXTRA_REGISTERED_EMAIL, etEmail.text.toString())
                    putExtra(LoginActivity.EXTRA_REGISTERED_PASSWORD, etPassword.text.toString())
                    startActivity(this)
                    this@RegisterActivity.finish()
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

        btnRegister.setOnClickListener {
            viewModel.onEvent(
                RegisterEvent.SendRegisterRequest(
                    name = etName.text.toString(),
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString()
                )
            )
        }

        btnLogin.setOnClickListener {
            Intent(this@RegisterActivity, LoginActivity::class.java).run {
                startActivity(this)
                finish()
            }
        }
    }
}