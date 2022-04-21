package com.romnan.dicodingstory.core.layers.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.romnan.dicodingstory.R
import com.romnan.dicodingstory.core.layers.data.paging.StoriesRemoteMediator
import com.romnan.dicodingstory.core.layers.data.retrofit.CoreApi
import com.romnan.dicodingstory.core.layers.data.retrofit.FakeCoreApi
import com.romnan.dicodingstory.core.layers.data.retrofit.model.GetStoriesResponse
import com.romnan.dicodingstory.core.layers.data.room.CoreDatabase
import com.romnan.dicodingstory.core.layers.data.room.dao.StoryDao
import com.romnan.dicodingstory.core.layers.data.room.dao.StoryRemoteKeysDao
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import com.romnan.dicodingstory.core.util.Resource
import com.romnan.dicodingstory.core.util.UIText
import com.romnan.dicodingstory.util.Faker
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

@SmallTest
class CoreRepositoryImplTest {

    private lateinit var coreDatabase: CoreDatabase

    private lateinit var storyDao: StoryDao
    private lateinit var storyRemoteKeysDao: StoryRemoteKeysDao

    private lateinit var fakePreferencesRepository: PreferencesRepository

    @Before
    fun setUp() {
        coreDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CoreDatabase::class.java
        ).allowMainThreadQueries().build()
        storyDao = coreDatabase.storyDao
        storyRemoteKeysDao = coreDatabase.storyRemoteKeysDao

        fakePreferencesRepository = FakePreferencesRepository(
            appPreferences = Faker.getAppPreferences(loggedIn = true)
        )
    }

    @After
    fun tearDown() {
        coreDatabase.close()
    }

    private fun createCoreRepositoryImpl(
        coreDatabase: CoreDatabase,
        coreApi: CoreApi
    ) = CoreRepositoryImpl(
        storyDao = coreDatabase.storyDao,
        coreApi = coreApi,
        preferencesRepository = fakePreferencesRepository,
        storiesRemoteMediator = StoriesRemoteMediator(
            coreDatabase = coreDatabase,
            coreApi = coreApi,
            preferencesRepository = fakePreferencesRepository
        )
    )

    @Test
    fun getStories_emitsLoadingFirst() = runBlocking {
        val fakeCoreApi = FakeCoreApi()
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun successGetStories_correctStoriesList() = runBlocking {
        val expectedStoriesList = Faker.getStoriesList(100)
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = false,
                listStory = expectedStoriesList,
                message = "success"
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Success::class.java)
                assertThat(it.data).isEqualTo(expectedStoriesList)
            }
            awaitComplete()
        }
    }

    @Test
    fun errorGetStoriesWithErrorMessage_correctErrorMessage() = runBlocking {
        val expectedErrorMessage = Faker.getLorem()
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = true,
                listStory = null,
                message = expectedErrorMessage
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.DynamicString(expectedErrorMessage))
            }
            awaitComplete()
        }
    }

    @Test
    fun errorGetStoriesWithoutErrorMessage_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = true,
                listStory = null,
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun nullResponseOnGetStories_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = null,
                listStory = null,
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun getStoriesEmptyStories_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = false,
                listStory = emptyList(),
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStories().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_stories_empty))
            }
            awaitComplete()
        }
    }

    @Test
    fun getStoriesWithLocation_emitsLoadingFirst() = runBlocking {
        val fakeCoreApi = FakeCoreApi()
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun successGetStoriesWithLocation_correctStoriesList() = runBlocking {
        val expectedStoriesList = Faker.getStoriesList(100)
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = false,
                listStory = expectedStoriesList,
                message = "success"
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Success::class.java)
                assertThat(it.data).isEqualTo(expectedStoriesList)
            }
            awaitComplete()
        }
    }

    @Test
    fun errorGetStoriesWithLocationWithErrorMessage_correctErrorMessage() = runBlocking {
        val expectedErrorMessage = Faker.getLorem()
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = true,
                listStory = null,
                message = expectedErrorMessage
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.DynamicString(expectedErrorMessage))
            }
            awaitComplete()
        }
    }

    @Test
    fun errorGetStoriesWithLocationWithoutErrorMessage_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = true,
                listStory = null,
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun nullResponseOnGetStoriesWithLocation_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = null,
                listStory = null,
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_unknown))
            }
            awaitComplete()
        }
    }

    @Test
    fun getStoriesWithLocationEmptyStories_correctErrorMessage() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            stories = GetStoriesResponse(
                error = false,
                listStory = emptyList(),
                message = null
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getStoriesWithLocation().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            awaitItem().let {
                assertThat(it).isInstanceOf(Resource.Error::class.java)
                assertThat(it.uiText).isEqualTo(UIText.StringResource(R.string.em_stories_empty))
            }
            awaitComplete()
        }
    }

    @Test
    fun getPagedStories_notNull() = runBlocking {
        val fakeCoreApi = FakeCoreApi(
            pagedStories = GetStoriesResponse(
                error = false,
                listStory = Faker.getStoriesList(),
                message = "success"
            )
        )
        val coreRepository = createCoreRepositoryImpl(coreDatabase, fakeCoreApi)

        coreRepository.getPagedStories().test {
            assertThat(awaitItem()).isNotNull()
        }
    }
}