package com.romnan.dicodingstory.features.addStory.presentation

import android.Manifest
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.romnan.dicodingstory.R
import org.junit.Rule
import org.junit.Test

@MediumTest
class AddStoryActivityTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    fun openAddStoryActivity_showActivityProperly() {
        launchActivity<AddStoryActivity>()

        onView(withId(R.id.tv_current_location)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_background)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_camera)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_gallery)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_preview_image)).check(matches(isDisplayed()))
    }
}