package com.punpuf.e_usp.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.punpuf.e_usp.R
import com.punpuf.e_usp.ui.library.LibraryFragment.SearchDisplay
import com.punpuf.e_usp.ui.theme.AppTheme
import com.punpuf.e_usp.ui.theme.Typography

@ExperimentalAnimationApi
@Composable
fun SearchBar(
    display: SearchDisplay,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: () -> Unit,
    onGoBack: () -> Unit,
    onFocus: () -> Unit,
    searching: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    
    AppSurface(
        color = AppTheme.colors.uiFloated,
        contentColor = AppTheme.colors.textSecondary,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(true) { onFocus() }
        ,
    ) {
        
        DisposableEffect(display) {
            if (display == SearchDisplay.Suggestions) focusRequester.requestFocus()
            onDispose {  }
        }
        
        Box(Modifier.fillMaxSize()) {
            
            if (query.text.isEmpty()) SearchHint()
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
            ) {
                // Return arrow
                if (display == SearchDisplay.Suggestions || display == SearchDisplay.SearchResults) {
                    IconButton(onClick = onGoBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            tint = AppTheme.colors.iconPrimary,
                            contentDescription = stringResource(R.string.label_back)
                        )
                    }
                }
                else Spacer(Modifier.width(IconSize)) // balance arrow icon
                
                if (display == SearchDisplay.Suggestions) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { if (it.isFocused) onFocus() }
                            .focusRequester(focusRequester)
                    )
                } else {
                    Text(
                        text = query.text,
                        style = Typography.body1,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f),
                    )
                }
                
                if (searching) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.iconPrimary,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(36.dp)
                    )
                } else Spacer(Modifier.width(IconSize)) // balance arrow icon
                
                if (display == SearchDisplay.Suggestions && query.text.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            tint = AppTheme.colors.iconPrimary,
                            contentDescription = stringResource(R.string.label_clear)
                        )
                    }
                }
            }
        }
    }
}

private val IconSize = 48.dp

@Composable
private fun SearchHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            tint = AppTheme.colors.textHelp,
            contentDescription = stringResource(R.string.label_search)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.search_app),
            color = AppTheme.colors.textHelp
        )
    }
}

@ExperimentalAnimationApi
@Preview("Search Bar")
@Composable
private fun SearchBarPreview() {
    AppTheme {
        AppSurface {
            SearchBar(
                query = TextFieldValue(""),
                onQueryChange = { },
                onSearch = {},
                onClearQuery = { },
                display = SearchDisplay.Suggestions,
                onGoBack = {},
                onFocus = {},
                searching = false,
            )
        }
    }
}

@ExperimentalAnimationApi
@Preview("Search Bar • Dark")
@Composable
private fun SearchBarDarkPreview() {
    AppTheme(isDarkTheme = true) {
        AppSurface {
            SearchBar(
                query = TextFieldValue(""),
                onQueryChange = { },
                onSearch = {},
                onGoBack = {},
                display = SearchDisplay.Suggestions,
                onClearQuery = { },
                onFocus = {},
                searching = false,
            )
        }
    }
}
