package com.romnan.dicodingstory.util

import android.location.Location
import android.net.Uri
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.features.addStory.domain.model.NewStory
import com.romnan.dicodingstory.features.login.data.model.LoginResponse
import java.io.File
import java.util.*

object Faker {
    fun getNewStory(withLocation: Boolean = false) = NewStory(
        description = getLorem(),
        photo = getJpegFile(),
        lat = if (withLocation) 69.420F else null,
        lon = if (withLocation) 42.069F else null
    )

    fun getName() = "John Doe #${System.currentTimeMillis()}"

    fun getEmail() = "${System.currentTimeMillis()}@gmail.com"

    fun getPassword() = "password${System.currentTimeMillis()}"

    fun getJpegFile(): File = File.createTempFile(System.currentTimeMillis().toString(), ".jpeg")

    fun getStoriesList(count: Int = 20): List<Story> {
        val storiesList = mutableListOf<Story>()
        for (i in 1..count) {
            storiesList.add(
                Story(
                    id = UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis().toString(),
                    name = "test_user$i",
                    photoUrl = "https://cdn.statically.io/og/Hello%20World$i.jpg",
                    description = "${getLorem()} $i",
                    lat = i.toDouble(),
                    lon = i.toDouble()
                )
            )
        }
        return storiesList
    }

    fun getStory(): Story = Story(
        id = UUID.randomUUID().toString(),
        createdAt = System.currentTimeMillis().toString(),
        name = "test_user",
        photoUrl = "https://cdn.statically.io/og/Hello%20World.jpg",
        description = getLorem(),
        lat = 69.420,
        lon = 42.069
    )

    fun getLocation(): Location =
        Location("fake_location").apply {
            latitude = 69.420
            longitude = 42.069
        }

    fun getFilledLoginResult(): LoginResult = LoginResult(
        userId = "user-lF_B0VPGcfuEGr0Q",
        name = "John Doe #${System.currentTimeMillis()}",
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWxGX0IwVlBHY2Z1RUdyMFEiLCJpYXQiOjE2NTAwOTMxMjl9.94LgJgI-R1lIfD9eq_oBW7w0JJJpY7oThT7AlizFDKI"
    )

    fun getAppPreferences(
        loggedIn: Boolean
    ): AppPreferences =
        if (loggedIn) AppPreferences(loginResult = getFilledLoginResult())
        else AppPreferences(loginResult = LoginResult.defaultValue)

    fun getUri(): Uri =
        Uri.parse("foo://example.com:8042/lorem/ipsum?time=${System.currentTimeMillis()}#dolor")

    fun getLorem() =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt."
}