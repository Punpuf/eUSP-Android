package com.punpuf.e_usp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.punpuf.e_usp.R
import com.punpuf.e_usp.ui.theme.AppTheme
import com.punpuf.e_usp.vo.Filter

@Composable
fun FilterBar(filters: List<Filter>) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
        modifier = Modifier.heightIn(min = 56.dp)
    ) {
        item {
            IconButton(onClick = { /* todo */ }) {
                Icon(
                    imageVector = Icons.Rounded.FilterList,
                    tint = AppTheme.colors.brand,
                    contentDescription = stringResource(R.string.filter_label),
                    modifier = Modifier.diagonalGradientBorder(
                        colors = AppTheme.colors.interactiveSecondary,
                        shape = CircleShape
                    )
                )
            }
        }
        items(filters) { filter ->
            FilterChip(filter)
        }
    }
}

@Composable
fun FilterChip(
    filter: Filter,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val (selected, setSelected) = filter.enabled
    val backgroundColor by animateColorAsState(
        if (selected) AppTheme.colors.brand else AppTheme.colors.uiBackground
    )
    val border = Modifier.fadeInDiagonalGradientBorder(
        showBorder = !selected,
        colors = AppTheme.colors.interactiveSecondary,
        shape = shape
    )
    val textColor by animateColorAsState(
        if (selected) AppTheme.colors.textInteractive else AppTheme.colors.textSecondary
    )
    AppSurface(
        modifier = modifier
            .height(28.dp)
            .then(border),
        color = backgroundColor,
        contentColor = textColor,
        shape = shape,
        elevation = 2.dp
    ) {
        Box(
            modifier = Modifier.toggleable(
                value = selected,
                onValueChange = setSelected
            )
        ) {
            Row {
                Icon(imageVector = filter.icon, contentDescription = null)
                Text(
                    text = filter.name,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 6.dp
                    )
                )    
            }
        }
    }
}
