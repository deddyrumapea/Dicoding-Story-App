package com.romnan.dicodingstory.features.storyDetail

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.presentation.model.StoryParcelable
import com.romnan.dicodingstory.util.Faker
import org.junit.Test

@MediumTest
class StoryDetailActivityTest {
    @Test
    fun openStoryDetail_ShowCorrectStoryDetailProperly() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val story = Faker.getStory()
        val intent = Intent(context, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailActivity.EXTRA_STORY_PARCELABLE, StoryParcelable(story))
        }

        launchActivity<StoryDetailActivity>(intent)

        onView(withId(R.id.iv_detail_photo)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_detail_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_detail_description)).check(matches(withText(story.description)))

        onView(withId(R.id.tv_detail_user_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_detail_user_name)).check(matches(withText(story.name)))
    }
}