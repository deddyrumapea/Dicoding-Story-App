package com.romnan.dicodingstory.features.storiesMap

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import org.junit.Test

@MediumTest
class StoriesMapActivityTest {
    @Test
    fun openStoriesMapActivity_showMapsProperly() {
        launchActivity<StoriesMapActivity>()
        onView(withId(R.id.map)).check(matches(isDisplayed()))
    }
}