package com.punpuf.e_usp.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.punpuf.e_usp.db.BookOfSearchDao
import com.punpuf.e_usp.network.LibraryNetworkService
import com.punpuf.e_usp.vo.BookOfSearch
import timber.log.Timber.d

@ExperimentalPagingApi
class LibraryRemoteMediator (
    //todo add filters
    private val bookOfSearchDao: BookOfSearchDao,
    private val libraryNetworkService: LibraryNetworkService, 
): RemoteMediator<Int, BookOfSearch>() {
    
    private var query = ""
    private var page = 1
    private var reachedEnd = false
    
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BookOfSearch>
    ): MediatorResult {
        d("Load being requested $query")
        val nextPage = when (loadType) {
            LoadType.REFRESH -> 1 // we're refreshing so just reset the page
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // shouldn't need to prepend
            LoadType.APPEND -> page + 1
        }
        
        return try {
            val response = libraryNetworkService.requestLibraryBookOfSearchList(
                page = nextPage,
                query = query,
            )
            
            page = nextPage
            reachedEnd = (response.pageNumber ?: 0) * (response.resultsPerPage ?: 0) >= (response.numResultsTotal ?: 1)

            if (loadType == LoadType.REFRESH) bookOfSearchDao.clearQuery(query)
            bookOfSearchDao.insertAll(response.bookOfSearchList)
            
            d("Items that were added are: $response\n ${response.bookOfSearchList}")
            
            MediatorResult.Success(endOfPaginationReached = reachedEnd)
            
        } catch (e: Exception) { MediatorResult.Error(e) }
        
    }

    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }
    
    fun setQuery(newQuery: String) {
        query = newQuery
        
    }
}