package com.punpuf.e_usp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), // for btm sheet
)
