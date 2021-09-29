package com.punpuf.e_usp.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.punpuf.e_usp.repo.LibraryRemoteMediator
import com.punpuf.e_usp.repo.LibraryRepository
import com.punpuf.e_usp.util.AbsentLiveData
import com.punpuf.e_usp.vo.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.d

@ExperimentalPagingApi
class LibraryViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val librarySearchMediator: LibraryRemoteMediator,
) : ViewModel() {
    val userInfo: LiveData<UserInfo?> = libraryRepository.getUserInfo()

    // loans section
    private val libraryLoanData = MediatorLiveData<Resource<List<BookUser>>>()
    private var libraryLoanSource: LiveData<Resource<List<BookUser>>>? = null
    private var libraryLoanJob: Job? = null
    val libraryLoans: LiveData<Resource<List<BookUser>>?> = Transformations.switchMap(userInfo) { user ->
        // end previous jobs
        libraryLoanJob?.cancel()
        if (libraryLoanSource != null) libraryLoanData.removeSource(libraryLoanSource!!)

        if (user == null) {
            AbsentLiveData.create()
        } else {
            libraryLoanJob = viewModelScope.launch {
                libraryLoanSource = libraryRepository.getLibraryLoans()
                libraryLoanData.addSource(libraryLoanSource!!) { libraryLoanData.postValue(it) }
            }
            libraryLoanData
        }
    }

    // reservations section
    private val libraryReservationData = MediatorLiveData<Resource<List<BookUser>>>()
    private var libraryReservationSource: LiveData<Resource<List<BookUser>>>? = null
    private var libraryReservationJob: Job? = null
    val libraryReservations: LiveData<Resource<List<BookUser>>?> = Transformations.switchMap(userInfo) { user ->
        // end previous jobs
        libraryReservationJob?.cancel()
        if (libraryReservationSource != null) libraryReservationData.removeSource(libraryReservationSource!!)

        if (user == null) {
            AbsentLiveData.create()
        } else {
            libraryReservationJob = viewModelScope.launch {
                libraryReservationSource = libraryRepository.getLibraryReservations()
                libraryReservationData.addSource(libraryReservationSource!!) { libraryReservationData.postValue(it) }
            }
            libraryReservationData
        }
    }

    // history section
    //private val libraryHistoryShow = MediatorLiveData<Boolean>()
    private val libraryHistoryData = MediatorLiveData<Resource<List<BookUser>>>()
    private var libraryHistorySource: LiveData<Resource<List<BookUser>>>? = null
    private var libraryHistoryJob: Job? = null
    val libraryHistory: LiveData<Resource<List<BookUser>>?> = Transformations.switchMap(userInfo) { user ->
        // end previous jobs
        libraryHistoryJob?.cancel()
        if (libraryHistorySource != null) libraryHistoryData.removeSource(libraryHistorySource!!)

        if (user == null) {
            AbsentLiveData.create()
        } else {
            libraryHistoryJob = viewModelScope.launch {
                libraryHistorySource = libraryRepository.getLibraryHistory()
                libraryHistoryData.addSource(libraryHistorySource!!) { libraryHistoryData.postValue(it) }
            }
            libraryHistoryData
        }
    }
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    fun setSearchQuery(query: String) {
        d("query being set $query")
        _searchQuery.value = query
        librarySearchMediator.setQuery(query)
    }
    
    //private var searchResultsData
    val searchResults: LiveData<Pager<Int, BookOfSearch>> = Transformations.switchMap(_searchQuery) { query ->
        d("Search results being updating $query")
        MutableLiveData(
            Pager(
                config = PagingConfig(10),
                remoteMediator = librarySearchMediator
            ) { libraryRepository.getSearchStuff(query) }
        )
    }    
    
    fun getSearchSuggestions(): List<String> {
        return emptyList()
    }
}