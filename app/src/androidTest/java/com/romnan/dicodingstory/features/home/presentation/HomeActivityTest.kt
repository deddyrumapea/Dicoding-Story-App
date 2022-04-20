package com.romnan.dicodingstory.features.home.presentation

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.DebugConfig
import com.romnan.dicodingstory.core.util.EspressoIdlingResource
import com.romnan.dicodingstory.util.JsonTestUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test


class HomeActivityTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        // IMPORTANT: application has to be in 'logged in' state
        // in order to run this test properly

        mockWebServer.start(8080)
        DebugConfig.BASE_URL = "http://127.0.0.1:8080/"

        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
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
}