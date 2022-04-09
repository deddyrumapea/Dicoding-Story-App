package com.romnan.dicodingstory.features.addStory.presentation

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.core.content.FileProvider
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.presentation.model.AddStoryEvent
import com.romnan.dicodingstory.features.home.presentation.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val viewModel: AddStoryViewModel by viewModels()

    private lateinit var currentJpgPath: String
    private var selectedJpg: File? = null

    private var ivPreview: ImageView? = null
    private var etDescription: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.add_story)
        setContentView(R.layout.activity_add_story)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        ivPreview = findViewById(R.id.iv_preview_image)

        etDescription = findViewById(R.id.et_description)
        val btnCamera = findViewById<Button>(R.id.btn_camera)
        val btnGallery = findViewById<Button>(R.id.btn_gallery)
        val pbUploading = findViewById<ProgressBar>(R.id.pb_uploading)

        btnCamera.setOnClickListener { launchCamera() }
        btnGallery.setOnClickListener { launchGallery() }

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
                startActivity(Intent(this@AddStoryActivity, HomeActivity::class.java))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.im_upload -> uploadImage()
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_story, menu)
        return true
    }

    private fun uploadImage() {
        val newStory = NewStory(
            description = etDescription?.text.toString(),
            photo = selectedJpg ?: return
        )

        viewModel.onEvent(AddStoryEvent.UploadImage(newStory))
    }

    private fun launchGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedJpgUri: Uri = result.data?.data as Uri
            selectedJpg = findFileByUri(selectedJpgUri, this@AddStoryActivity)
            ivPreview?.setImageURI(selectedJpgUri)
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            resolveActivity(packageManager)
        }

        createTempJpg(application).also { tempJpg ->
            val tempJpgUri: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.romnan.dicodingstory",
                tempJpg
            )
            currentJpgPath = tempJpg.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempJpgUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            selectedJpg = File(currentJpgPath)

            val result = BitmapFactory.decodeFile(selectedJpg?.path)
            ivPreview?.setImageBitmap(result)
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
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}