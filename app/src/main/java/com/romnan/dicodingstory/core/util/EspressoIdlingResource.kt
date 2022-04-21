package com.romnan.dicodingstory.core.util

import androidx.test.espresso.idling.CountingIdlingResource
import com.romnan.dicodingstory.BuildConfig

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> espressoIdlingResource(function: () -> T): T {
    if (!BuildConfig.DEBUG) return function()

    EspressoIdlingResource.increment()
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement()
    }
}