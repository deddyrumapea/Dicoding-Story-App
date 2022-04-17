package com.romnan.dicodingstory.util

import android.location.Location
import android.net.Uri
import com.romnan.dicodingstory.core.layers.domain.model.AppPreferences
import com.romnan.dicodingstory.core.layers.domain.model.LoginResult
import com.romnan.dicodingstory.core.layers.domain.model.Story
import java.io.File
import java.util.*

object Faker {
    fun getName() = "John Doe #${System.currentTimeMillis()}"

    fun getEmail() = "${System.currentTimeMillis()}@gmail.com"

    fun getPassword() = "password${System.currentTimeMillis()}"

    fun getJpegFile() = File.createTempFile(System.currentTimeMillis().toString(), ".jpeg")

    fun getStoriesList(count: Int = 20): List<Story> {
        val storiesList = mutableListOf<Story>()
        for (i in 1..count) {
            storiesList.add(
                Story(
                    id = UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis().toString(),
                    name = "testuser$i",
                    photoUrl = "https://cdn.statically.io/og/Hello%20World$i.jpg",
                    description = "Lorem ipsum dolor sit amet $i",
                    lat = i.toDouble(),
                    lon = i.toDouble()
                )
            )
        }
        return storiesList
    }

    fun getLocation(): Location =
        Location("fakelocation").apply {
            latitude = 69.420
            longitude = 42.69
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