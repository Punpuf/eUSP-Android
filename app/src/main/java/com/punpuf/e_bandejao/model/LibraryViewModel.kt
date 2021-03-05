package com.punpuf.e_bandejao.model

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.punpuf.e_bandejao.repo.LibraryRepository
import com.punpuf.e_bandejao.util.AbsentLiveData
import com.punpuf.e_bandejao.vo.BookUser
import com.punpuf.e_bandejao.vo.Resource
import com.punpuf.e_bandejao.vo.UserInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibraryViewModel @ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {
    val userInfo: LiveData<UserInfo?> = libraryRepository.getUserInfo()

    // loans section
    private val libraryLoanData = MediatorLiveData<Resource<List<BookUser>>>()
    private var libraryLoanSource: LiveData<Resource<List<BookUser>>>? = null
    private var libraryLoanJob: Job? = null
    val libraryLoans: LiveData<Resource<List<BookUser>>> = Transformations.switchMap(userInfo) { user ->
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
    val libraryReservations: LiveData<Resource<List<BookUser>>> = Transformations.switchMap(userInfo) { user ->
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
    val libraryHistory: LiveData<Resource<List<BookUser>>> = Transformations.switchMap(userInfo) { user ->
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
}