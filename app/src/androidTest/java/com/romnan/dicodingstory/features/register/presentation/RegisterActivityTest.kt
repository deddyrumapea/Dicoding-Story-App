package com.romnan.dicodingstory.features.register.presentation

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import org.junit.Test

@MediumTest
class RegisterActivityTest {
    @Test
    fun openRegisterActivity_showRegisterFormProperly() {
        launchActivity<RegisterActivity>()

        onView(withId(R.id.et_register_name)).check(matches(isDisplayed()))
        onView(withId(R.id.eet_register)).check(matches(isDisplayed()))
        onView(withId(R.id.pet_register)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
    }
}