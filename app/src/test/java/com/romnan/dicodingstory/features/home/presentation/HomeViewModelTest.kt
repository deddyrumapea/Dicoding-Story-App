package com.romnan.dicodingstory.features.home.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.romnan.dicodingstory.Faker
import com.romnan.dicodingstory.MainCoroutineRule
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.features.home.presentation.adapter.StoriesPagingAdapter
import com.romnan.dicodingstory.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var homeViewModel: HomeViewModel

    @Test
    fun `getStoriesList(), not null`() = runTest {
        val storiesList = Faker.getStoriesList()
        val pagingData = PagedTestDataSources.snapshot(storiesList)
        val storiesLiveData = MutableLiveData<PagingData<Story>>()
        storiesLiveData.value = pagingData

        Mockito.`when`(homeViewModel.storiesList).thenReturn(storiesLiveData)
        val actualStories = homeViewModel.storiesList.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()
        Mockito.verify(homeViewModel).storiesList
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(storiesList.size, differ.snapshot().size)
        Assert.assertEquals(storiesList[0].id, differ.snapshot()[0]?.id)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class PagedTestDataSources private constructor(
    private val items: List<Story>
) : PagingSource<Int, LiveData<List<Story>>>() {

    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, LiveData<List<Story>>>
    ): Int {
        return 0
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}