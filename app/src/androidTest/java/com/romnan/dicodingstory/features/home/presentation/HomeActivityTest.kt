package com.romnan.dicodingstory.features.home.presentation

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.DebugConfig
import com.romnan.dicodingstory.core.util.EspressoIdlingResource
import com.romnan.dicodingstory.features.storyDetail.StoryDetailActivity
import com.romnan.dicodingstory.util.JsonTestUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        DebugConfig.BASE_URL = "http://127.0.0.1:8080/"
        DebugConfig.ALLOW_AUTO_START_ACTIVITY = false

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    @MediumTest
    fun successGetStories_showStoriesProperly() {
        launchActivity<HomeActivity>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonTestUtil.readStringFromFile("stories_success.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.rv_stories_list)).check(matches(isDisplayed()))
        onView(withText("testUser1")).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories_list)).perform(
            RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                hasDescendant(withText("testUser10"))
            )
        )
    }

    @Test
    @LargeTest
    fun clickOnAStory_showCorrectStoryDetail() {
        Intents.init()

        launchActivity<HomeActivity>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonTestUtil.readStringFromFile("stories_success.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withText("testUser1")).perform(click())
        intended(hasComponent(StoryDetailActivity::class.java.name))

        onView(withId(R.id.iv_detail_photo)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_detail_user_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_detail_user_name)).check(matches(withText("testUser1")))

        onView(withId(R.id.tv_detail_description)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_detail_description)).check(matches(withText("Lorem ipsum dolor sit amet")))
    }
}