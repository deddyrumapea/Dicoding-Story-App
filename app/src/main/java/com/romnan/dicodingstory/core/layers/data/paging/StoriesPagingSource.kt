package com.romnan.dicodingstory.core.layers.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.romnan.dicodingstory.core.layers.data.retrofit.CoreApi
import com.romnan.dicodingstory.core.layers.domain.model.Story
import com.romnan.dicodingstory.core.layers.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first

class StoriesPagingSource(
    private val api: CoreApi,
    private val prefRepo: PreferencesRepository
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        try {
            val loginResult = prefRepo.getAppPreferences().first().loginResult
            val bearerToken = "Bearer ${loginResult.token}"

            val position = params.key ?: INITIAL_PAGE_INDEX

            val response = api.getPagedStories(
                bearerToken = bearerToken,
                page = position,
                size = params.loadSize
            )

            val storiesList = response.listStory ?: emptyList()

            return LoadResult.Page(
                data = storiesList,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (storiesList.isNullOrEmpty()) null else position + 1
            )
        } catch (t: Throwable) {
            return LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}