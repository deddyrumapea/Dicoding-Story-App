package com.romnan.dicodingstory.core.layers.data.util

import androidx.datastore.core.Serializer
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppPreferencesSerializer : Serializer<AppPreferences> {
    override val defaultValue: AppPreferences
        get() = AppPreferences.defaultValue

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return try {
            Json.decodeFromString(
                deserializer = AppPreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppPreferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}