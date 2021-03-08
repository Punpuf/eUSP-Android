package com.punpuf.e_usp.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.punpuf.e_usp.vo.Filter
import com.punpuf.e_usp.vo.Publication

@Composable
fun SearchResults(
    query: String,
    searchResults: List<Publication>,
    filters: List<Filter>,
    onPublicationClick: (Publication) -> Unit,
) {
    Column {
        /*FilterBar(filters)
        Text(
            text = stringResource(R.string.search_count, searchResults.size),
            style = MaterialTheme.typography.h6,
            color = JetsnackTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        LazyColumn {
            itemsIndexed(searchResults) { index, snack ->
                SearchResult(snack, onSnackClick, index != 0)
            }
        }*/
    }
}