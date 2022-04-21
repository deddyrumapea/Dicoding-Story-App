package com.romnan.dicodingstory.features.login.presentation

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @Test
    @MediumTest
    fun openLoginActivity_showLoginFormProperly() {
        launchActivity<LoginActivity>()
        onView(withId(R.id.eet_login)).check(matches(isDisplayed()))
        onView(withId(R.id.pet_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_go_to_register)).check(matches(isDisplayed()))
    }
}