package com.romnan.dicodingstory.features.preferences

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import org.junit.Test

@MediumTest
class PreferencesActivityTest {
    @Test
    fun openPreferencesActivity_showPreferencesProperly() {
        launchActivity<PreferencesActivity>()

        onView(withId(R.id.tv_title_language)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_description_language)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_open_phone_language_settings)).check(matches(isDisplayed()))
    }
}