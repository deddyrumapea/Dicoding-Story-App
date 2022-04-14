package com.romnan.dicodingstory

import com.romnan.dicodingstory.core.layers.domain.model.Story
import java.util.*

object Faker {
    fun getStoriesList(count: Int = 100): List<Story> {
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
}