package com.romnan.dicodingstory.features.register.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.presentation.customView.PasswordEditText
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
        val eetRegister = findViewById<EditText>(R.id.eet_register)
        val petRegister = findViewById<PasswordEditText>(R.id.pet_register)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        viewModel.isLoading.observe(this) { isLoading ->
            etName.isEnabled = !isLoading
            eetRegister.isEnabled = !isLoading
            petRegister.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
            btnRegister.text =
                if (isLoading) getString(R.string.registering) else getString(R.string.register)
        }

        viewModel.isRegistered.observe(this) { isRegistered ->
            if (isRegistered) {
                Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    putExtra(LoginActivity.EXTRA_REGISTERED_EMAIL, eetRegister.text.toString())
                    putExtra(LoginActivity.EXTRA_REGISTERED_PASSWORD, petRegister.text.toString())
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
                    email = eetRegister.text.toString(),
                    password = petRegister.text.toString()
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }
}