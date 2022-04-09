package com.romnan.dicodingstory.features.addStory.presentation

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.presentation.model.AddStoryEvent
import com.romnan.dicodingstory.features.home.presentation.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val viewModel: AddStoryViewModel by viewModels()

    private var etDescription: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.add_story)
        setContentView(R.layout.activity_add_story)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE
            )
        }

        etDescription = findViewById(R.id.et_description)
        val ivPreview = findViewById<ImageView>(R.id.iv_preview_image)
        val btnCamera = findViewById<Button>(R.id.btn_camera)
        val btnGallery = findViewById<Button>(R.id.btn_gallery)
        val pbUploading = findViewById<ProgressBar>(R.id.pb_uploading)

        btnCamera.setOnClickListener { launchCamera() }
        btnGallery.setOnClickListener { launchGallery() }

        viewModel.photoFile.observe(this) { ivPreview.setImageURI(it.toUri()) }

        viewModel.errorMessage.observe(this) { uiText ->
            val message = when (uiText) {
                is UIText.DynamicString -> uiText.value
                is UIText.StringResource -> getString(uiText.id)
            }

            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isUploaded.observe(this) { isUploaded ->
            if (isUploaded) {
                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
                startActivity(
                    Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }
        }

        viewModel.isUploading.observe(this) { isUploading ->
            btnCamera.isEnabled = !isUploading
            btnGallery.isEnabled = !isUploading
            etDescription?.isEnabled = !isUploading
            pbUploading.visibility = if (isUploading) View.VISIBLE else View.GONE
        }
    }

    private fun uploadStory() {
        viewModel.onEvent(AddStoryEvent.UploadImage(etDescription?.text.toString()))
    }

    private fun launchGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = getString(R.string.image_all_types)
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedJpegUri: Uri = result.data?.data ?: return@registerForActivityResult
            viewModel.onEvent(AddStoryEvent.ImageSelected(selectedJpegUri))
        }
    }

    private fun launchCamera() {
        viewModel.onEvent(AddStoryEvent.OpenCamera)
        launcherIntentCamera.launch(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                resolveActivity(packageManager)
                putExtra(MediaStore.EXTRA_OUTPUT, viewModel.tempJpegUri)
            }
        )
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            viewModel.onEvent(AddStoryEvent.ImageCaptured)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.unable_to_acquire_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.im_upload -> uploadStory()
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_story, menu)
        return true
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 10
    }
}