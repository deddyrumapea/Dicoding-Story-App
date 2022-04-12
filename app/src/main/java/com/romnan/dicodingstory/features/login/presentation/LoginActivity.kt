package com.romnan.dicodingstory.features.login.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.presentation.customView.EmailEditText
import com.romnan.dicodingstory.core.layers.presentation.customView.PasswordEditText
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

        val eetLogin = findViewById<EmailEditText>(R.id.eet_login)
        val petLogin = findViewById<PasswordEditText>(R.id.pet_login)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnRegister = findViewById<Button>(R.id.btn_go_to_register)

        intent.getStringExtra(EXTRA_REGISTERED_EMAIL)?.let { eetLogin.setText(it) }
        intent.getStringExtra(EXTRA_REGISTERED_PASSWORD)?.let { petLogin.setText(it) }

        viewModel.isLoading.observe(this) { isLoading ->
            eetLogin.isEnabled = !isLoading
            petLogin.isEnabled = !isLoading
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
                    email = eetLogin.text.toString(),
                    password = petLogin.text.toString()
                )
            )
        }

        btnRegister.setOnClickListener {
            val animationBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@LoginActivity,
                Pair(eetLogin, getString(R.string.tn_et_register_email)),
                Pair(petLogin, getString(R.string.tn_et_register_password)),
                Pair(btnLogin, getString(R.string.tn_btn_register))
            ).toBundle()

            startActivity(
                Intent(this@LoginActivity, RegisterActivity::class.java),
                animationBundle
            )
        }
    }

    companion object {
        const val EXTRA_REGISTERED_EMAIL = "extra_registered_email"
        const val EXTRA_REGISTERED_PASSWORD = "extra_registered_password"
    }
}