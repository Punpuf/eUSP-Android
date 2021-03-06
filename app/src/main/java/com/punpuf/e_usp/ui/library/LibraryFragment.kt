package com.punpuf.e_usp.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import com.punpuf.e_usp.R
import com.punpuf.e_usp.model.LibraryViewModel
import com.punpuf.e_usp.ui.components.*
import com.punpuf.e_usp.ui.theme.AlphaNearOpaque
import com.punpuf.e_usp.ui.theme.AppTheme
import com.punpuf.e_usp.vo.Filter
import com.punpuf.e_usp.vo.FilterTypes
import com.punpuf.e_usp.vo.BookOfSearch
import com.punpuf.e_usp.vo.Resource
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import kotlinx.android.synthetic.main.fragment_library.view.*

@OptIn(ExperimentalPagingApi::class, ExperimentalAnimationApi:: class)
@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private val model: LibraryViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_library, container, false).apply {
        libraryComposeView.setContent {
            ProvideWindowInsets {
                AppTheme {
                    AppSurface(modifier = Modifier.fillMaxSize()) {
                        SearchScreen(
                            onPublicationClick = { } // todo implement
                        )
                    }
                }
            }
        }
    }
    
    @ExperimentalMaterialApi
    @Composable
    fun SearchScreen(
        modifier: Modifier = Modifier,
        state: SearchState = rememberSearchState(),
        onPublicationClick: (BookOfSearch) -> Unit,
    ) {
        Box(modifier = modifier) {
            // Search Bar
            Column(
                modifier = Modifier.background(AppTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque)),
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())
                SearchBar(
                    display = state.searchDisplay,
                    query = state.query,
                    onSearch = {
                        model.setSearchQuery(state.query.text)
                        state.searchDisplay = SearchDisplay.SearchResults
                               },
                    onQueryChange = { state.query = it },
                    onFocus = { state.searchDisplay = SearchDisplay.Suggestions },
                    onGoBack = {
                        state.query = TextFieldValue("")
                        state.searchDisplay = SearchDisplay.Home
                    },
                    onClearQuery = { state.query = TextFieldValue("") },
                    searching = state.searching
                )
                AppDivider()
            }

            // Content of page
            when (state.searchDisplay) {
                SearchDisplay.Home -> {
                    val loanList by model.libraryLoans.observeAsState(Resource.Loading())
                    val reservationList by model.libraryReservations.observeAsState(Resource.Loading())
                    val historyList by model.libraryHistory.observeAsState(Resource.Loading())

                    LibraryHome(
                        loanList = loanList,
                        reservationList = reservationList,
                        historyList = historyList,
                        modifier = Modifier.zIndex(-1f)
                    )
                }
                SearchDisplay.Suggestions -> SearchSuggestions(
                    suggestions = state.suggestions,
                    onSuggestionSelect = { suggestion -> state.query = TextFieldValue(suggestion) },
                )
                SearchDisplay.SearchResults -> SearchResults(
                    emptyList(),
                    {},
                    model,
                    modifier = Modifier.zIndex(-1f)
                )
            }

        }

    }


    // What the search screen should be displaying currently
    enum class SearchDisplay {
        Home, Suggestions, SearchResults
    }

    @Composable
    fun rememberSearchState(
        query: TextFieldValue = TextFieldValue(""),
        searching: Boolean = false,
        suggestions: List<String> = model.getSearchSuggestions(),
        filters: List<Filter> = getSearchFilters(),
        searchResults: List<BookOfSearch> = emptyList(),
        searchDisplay: SearchDisplay = SearchDisplay.Home,
    ): SearchState {
        return remember {
            SearchState(
                query = query,
                searching = searching,
                suggestions = suggestions,
                filters = filters,
                searchResults = searchResults,
                searchDisplay = searchDisplay,
            )
        }
    }

    @Stable
    class SearchState(
        query: TextFieldValue,
        searching: Boolean,
        suggestions: List<String> = emptyList(),
        filters: List<Filter>,
        searchResults: List<BookOfSearch>,
        searchDisplay: SearchDisplay,
        focusRequester: FocusRequester = FocusRequester(),
    ) {
        var query by mutableStateOf(query)
        var searching by mutableStateOf(searching)
        var suggestions by mutableStateOf(suggestions)
        var filters by mutableStateOf(filters)
        var searchResults by mutableStateOf(searchResults)
        var searchDisplay by mutableStateOf(searchDisplay)
        val focusRequester by mutableStateOf(focusRequester)
    }
    
    @Composable
    fun getSearchFilters(): List<Filter> {
        return listOf(
            Filter(
                type = FilterTypes.Location,
                name = stringResource(R.string.filter_location),
                icon = Icons.Rounded.Place,
            ),
            Filter(
                type = FilterTypes.MaterialType,
                name = stringResource(R.string.filter_material_type),
                icon = Icons.Rounded.Category,
            ),
            Filter(
                type = FilterTypes.SearchType,
                name = stringResource(R.string.filter_search_type),
                icon = Icons.Rounded.ManageSearch,
            ),
        )
    }
    
}