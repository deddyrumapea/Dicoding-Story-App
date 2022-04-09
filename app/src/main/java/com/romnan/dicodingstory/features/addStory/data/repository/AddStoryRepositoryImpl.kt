package com.romnan.dicodingstory.features.addStory.data.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.SimpleResource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.features.addStory.data.remote.AddStoryApi
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.addStory.domain.repository.AddStoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddStoryRepositoryImpl(
    private val api: AddStoryApi,
    private val appContext: Context,
    private val prefRepo: PreferencesRepository
) : AddStoryRepository {

    override fun uploadStory(newStory: NewStory): Flow<SimpleResource> = flow {
        emit(Resource.Loading())

        try {
            val loginResult = prefRepo.getAppPreferences().first().loginResult
            val bearerToken = "Bearer ${loginResult.token}"

            val rbDescription = newStory.description.toRequestBody("text/plain".toMediaType())
            val rbPhoto = compressJpeg(newStory.photo)
                .asRequestBody("image/jpeg".toMediaTypeOrNull())

            val photoMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                name = "photo",
                filename = newStory.photo.name,
                body = rbPhoto
            )

            val response = api.uploadStory(
                bearerToken = bearerToken,
                photo = photoMultipart,
                description = rbDescription
            )
            when {
                response.error != true -> emit(Resource.Success(Unit))
                response.message != null -> emit(
                    Resource.Error(UIText.DynamicString(response.message))
                )
                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }

        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    emit(Resource.Error(UIText.StringResource(R.string.em_http_exception)))
                }

                is IOException -> emit(
                    Resource.Error(UIText.StringResource(R.string.em_io_exception))
                )

                else -> emit(Resource.Error(UIText.StringResource(R.string.em_unknown)))
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun compressJpeg(file: File): File = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return@withContext file
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNewTempJpeg(): File = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
        val timeStamp: String = sdf.format(System.currentTimeMillis())

        val dirPictures: File? = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return@withContext File.createTempFile(timeStamp, ".jpeg", dirPictures)
    }

    override suspend fun getNewTempJpegUri(): Uri = withContext(Dispatchers.IO) {
        val tempJpeg = getNewTempJpeg()
        return@withContext FileProvider.getUriForFile(
            appContext,
            appContext.packageName,
            tempJpeg
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun findJpegByUri(uri: Uri): File = withContext(Dispatchers.IO) {
        val contentResolver: ContentResolver = appContext.contentResolver
        val tempJpeg = getNewTempJpeg()

        val inputStream: InputStream =
            contentResolver.openInputStream(uri) ?: return@withContext tempJpeg
        val outputStream: OutputStream = FileOutputStream(tempJpeg)

        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

        outputStream.close()
        inputStream.close()

        return@withContext tempJpeg
    }
}