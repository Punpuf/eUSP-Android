package com.punpuf.e_usp.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.punpuf.e_usp.R
import com.punpuf.e_usp.model.LibraryViewModel
import com.punpuf.e_usp.ui.components.AppDivider
import com.punpuf.e_usp.ui.theme.Typography
import com.punpuf.e_usp.vo.Filter
import com.punpuf.e_usp.vo.BookOfSearch
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import timber.log.Timber.d

@OptIn(ExperimentalPagingApi::class)
@Composable
fun SearchResults(
    filters: List<Filter>,
    onBookClick: (BookOfSearch) -> Unit,
    model: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    
    val query = model.searchQuery.observeAsState("")
    val results = model.searchResults.observeAsState()
    val lazyPagedItems = model.searchResults.value?.flow?.collectAsLazyPagingItems()
    
    d("query: ${query.value}")
    d("results: ${lazyPagedItems?.itemCount} ${lazyPagedItems} ")
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        if (lazyPagedItems != null) {
            item { 
                Spacer(modifier = Modifier
                    .statusBarsPadding()
                    .height(56.dp))
            }
            items(lazyPagedItems) { item ->
                BookOfSearchItem(
                    bookOfSearch = item,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Composable
fun BookOfSearchItem(
    bookOfSearch: BookOfSearch?,
    onBookClick: (BookOfSearch) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (bookOfSearch != null) onBookClick(bookOfSearch) }
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = bookOfSearch?.title ?: "",
            style = Typography.h6,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = if (bookOfSearch?.authors?.isEmpty() == true) stringResource(R.string.search_no_author) 
                else bookOfSearch?.authors ?: "",
            style = Typography.body1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        //AppDivider()
    }
}