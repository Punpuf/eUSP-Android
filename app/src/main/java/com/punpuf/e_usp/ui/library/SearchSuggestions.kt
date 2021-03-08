package com.punpuf.e_usp.ui.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.punpuf.e_usp.R
import com.punpuf.e_usp.ui.components.AppSurface
import com.punpuf.e_usp.ui.theme.AppTheme


@ExperimentalAnimationApi
@Composable
fun SearchSuggestions(
    suggestions: List<String>,
    onSuggestionSelect: (String) -> Unit
) {
    AnimatedVisibility(visible = suggestions.isNotEmpty()) {
        LazyColumn {
            item {
                SuggestionHeader(stringResource(R.string.search_suggestions_recent))
            }
            items(suggestions) { suggestion ->
                Suggestion(
                    suggestion = suggestion,
                    onSuggestionSelect = onSuggestionSelect,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SuggestionHeader(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.h6,
        color = AppTheme.colors.textPrimary,
        modifier = modifier
            .heightIn(min = 56.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .wrapContentHeight()
    )
}

@Composable
private fun Suggestion(
    suggestion: String,
    onSuggestionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = suggestion,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
            .heightIn(min = 48.dp)
            .clickable { onSuggestionSelect(suggestion) }
            .padding(start = 24.dp)
            .wrapContentSize(Alignment.CenterStart)
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewSuggestions() {
    AppTheme {
        AppSurface {
            SearchSuggestions(
                suggestions = listOf("Calculo", "EDO", "Circuitos Eletricos"),
                onSuggestionSelect = { }
            )
        }
    }
}
