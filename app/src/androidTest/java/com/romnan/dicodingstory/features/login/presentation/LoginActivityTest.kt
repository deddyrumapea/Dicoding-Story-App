package com.romnan.dicodingstory.features.login.presentation

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.util.DebugConfig
import com.romnan.dicodingstory.core.util.EspressoIdlingResource
import com.romnan.dicodingstory.features.home.presentation.HomeActivity
import com.romnan.dicodingstory.util.JsonTestUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

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
    fun openLoginActivity_showLoginFormProperly() {
        launchActivity<LoginActivity>()
        onView(withId(R.id.eet_login)).check(matches(isDisplayed()))
        onView(withId(R.id.pet_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_go_to_register)).check(matches(isDisplayed()))
    }

    @Test
    @LargeTest
    fun successLogin_openHomeActivity() {
        launchActivity<LoginActivity>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonTestUtil.readStringFromFile("login_success.json"))
        mockWebServer.enqueue(mockResponse)

        DebugConfig.ALLOW_AUTO_START_ACTIVITY = true

        onView(withId(R.id.eet_login)).perform(typeText("testing@testing.com"))
        onView(withId(R.id.pet_login)).perform(typeText("testing"))
        onView(withId(R.id.btn_login)).perform(click())

        Intents.intended(IntentMatchers.hasComponent(HomeActivity::class.java.name))
    }
}