package com.punpuf.e_usp.vo

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

@Stable
class Filter(
    val name: String,
    enabled: Boolean = false
) {
    val enabled = mutableStateOf(enabled)
}

val filters = listOf(
    Filter(name = "Organic"),
    Filter(name = "Gluten-free"),
    Filter(name = "Dairy-free"),
    Filter(name = "Sweet"),
    Filter(name = "Savory")
)
