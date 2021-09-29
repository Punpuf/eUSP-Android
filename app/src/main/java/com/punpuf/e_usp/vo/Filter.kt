package com.punpuf.e_usp.vo

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
class Filter(
    val type: FilterTypes,
    val name: String,
    val icon: ImageVector,
    enabled: Boolean = false,
) {
    val enabled = mutableStateOf(enabled)
}

enum class FilterTypes {
    Location, 
    SearchType, 
    MaterialType,
}
