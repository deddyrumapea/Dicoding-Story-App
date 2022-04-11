package com.romnan.dicodingstory.features.preferences

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.romnan.dicodingstory.R

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val btnGoToPhoneLangSettings = findViewById<Button>(R.id.btn_open_phone_language_settings)

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        btnGoToPhoneLangSettings.setOnClickListener {
            resultLauncher.launch(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }
}