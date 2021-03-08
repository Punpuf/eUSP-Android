package com.punpuf.e_usp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.punpuf.e_usp.ui.utils.LocalSysUiController

private val LightColorPalette = AppColors(
    brand = UspGalaxy500,
    uiBackground = Neutral0,
    uiBorder = Neutral4,
    uiFloated = FunctionalGrey,
    textSecondary = Neutral7,
    textHelp = Neutral6,
    textInteractive = Neutral0,
    textLink = UspGalaxy900,
    iconSecondary = Neutral7,
    iconInteractive = Neutral0,
    iconInteractiveInactive = Neutral1,
    error = FunctionalRed,
    gradient6_2A = listOf(UspGalaxy500, UspAmber500, UspGalaxy200, UspAmber500, UspGalaxy500),
    gradient6_2B = listOf(Rose4, Lavender3, Rose2, Lavender3, Rose4),
    gradient3_2A = listOf(UspAmber100, UspGalaxy300, UspLake50), //listOf(Shadow2, Ocean3, Shadow4),
    gradient3_2B = listOf(UspAmber600, UspAmber200, UspAmber300), //listOf(Rose2, Lavender3, Rose4),
    gradient2_1 = listOf(UspGalaxy200, UspAmber300),
    gradient2_2 = listOf(UspLake500, UspGalaxy300),
    isDark = false,
)

private val DarkColorPalette = AppColors(
    brand = Shadow1,
    uiBackground = Neutral8,
    uiBorder = Neutral3,
    uiFloated = FunctionalDarkGrey,
    textPrimary = Shadow1,
    textSecondary = Neutral0,
    textHelp = Neutral1,
    textInteractive = Neutral7,
    textLink = Ocean2,
    iconPrimary = Shadow1,
    iconSecondary = Neutral0,
    iconInteractive = Neutral7,
    iconInteractiveInactive = Neutral6,
    error = FunctionalRedDark,
    gradient6_2A = listOf(Shadow5, Ocean7, Shadow9, Ocean7, Shadow5),
    gradient6_2B = listOf(Rose11, Lavender7, Rose8, Lavender7, Rose11),
    gradient3_2A = listOf(Shadow9, Ocean7, Shadow5),
    gradient3_2B = listOf(Rose8, Lavender7, Rose11),
    gradient2_1 = listOf(UspGalaxy400, UspAmber300),
    gradient2_2 = listOf(UspLake800, UspGalaxy200),
    isDark = true,
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette

    val sysUiController = LocalSysUiController.current
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = colors.uiBackground.copy(alpha = AlphaNearOpaque)
        )
    }

    ProvideAppColors(colors) {
        MaterialTheme(
            colors = debugColors(isDarkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalJetsnackColors.current
}

/**
 * App custom Color Palette
 */
@Stable
class AppColors(
    gradient6_2A: List<Color>,
    gradient6_2B: List<Color>,
    gradient3_2A: List<Color>,
    gradient3_2B: List<Color>,
    gradient2_1: List<Color>,
    gradient2_2: List<Color>,
    brand: Color,
    uiBackground: Color,
    uiBorder: Color,
    uiFloated: Color,
    interactivePrimary: List<Color> = gradient2_1,
    interactiveSecondary: List<Color> = gradient2_2,
    interactiveMask: List<Color> = gradient6_2A,
    textPrimary: Color = brand,
    textSecondary: Color,
    textHelp: Color,
    textInteractive: Color,
    textLink: Color,
    iconPrimary: Color = brand,
    iconSecondary: Color,
    iconInteractive: Color,
    iconInteractiveInactive: Color,
    error: Color,
    notificationBadge: Color = error,
    isDark: Boolean
) {
    var gradient6_2A by mutableStateOf(gradient6_2A)
        private set
    var gradient6_2B by mutableStateOf(gradient6_2B)
        private set
    var gradient3_2A by mutableStateOf(gradient3_2A)
        private set
    var gradient3_2B by mutableStateOf(gradient3_2B)
        private set
    var gradient2_1 by mutableStateOf(gradient2_1)
        private set
    var gradient2_2 by mutableStateOf(gradient2_2)
        private set
    var brand by mutableStateOf(brand)
        private set
    var uiBackground by mutableStateOf(uiBackground)
        private set
    var uiBorder by mutableStateOf(uiBorder)
        private set
    var uiFloated by mutableStateOf(uiFloated)
        private set
    var interactivePrimary by mutableStateOf(interactivePrimary)
        private set
    var interactiveSecondary by mutableStateOf(interactiveSecondary)
        private set
    var interactiveMask by mutableStateOf(interactiveMask)
        private set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var textSecondary by mutableStateOf(textSecondary)
        private set
    var textHelp by mutableStateOf(textHelp)
        private set
    var textInteractive by mutableStateOf(textInteractive)
        private set
    var textLink by mutableStateOf(textLink)
        private set
    var iconPrimary by mutableStateOf(iconPrimary)
        private set
    var iconSecondary by mutableStateOf(iconSecondary)
        private set
    var iconInteractive by mutableStateOf(iconInteractive)
        private set
    var iconInteractiveInactive by mutableStateOf(iconInteractiveInactive)
        private set
    var error by mutableStateOf(error)
        private set
    var notificationBadge by mutableStateOf(notificationBadge)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: AppColors) {
        gradient6_2A = other.gradient6_2A
        gradient6_2B = other.gradient6_2B
        gradient3_2A = other.gradient3_2A
        gradient3_2B = other.gradient3_2B
        gradient2_1 = other.gradient2_1
        gradient2_2 = other.gradient2_2
        brand = other.brand
        uiBackground = other.uiBackground
        uiBorder = other.uiBorder
        uiFloated = other.uiFloated
        interactivePrimary = other.interactivePrimary
        interactiveSecondary = other.interactiveSecondary
        interactiveMask = other.interactiveMask
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        textHelp = other.textHelp
        textInteractive = other.textInteractive
        textLink = other.textLink
        iconPrimary = other.iconPrimary
        iconSecondary = other.iconSecondary
        iconInteractive = other.iconInteractive
        iconInteractiveInactive = other.iconInteractiveInactive
        error = other.error
        notificationBadge = other.notificationBadge
        isDark = other.isDark
    }
}

@Composable
fun ProvideAppColors(
    colors: AppColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalJetsnackColors provides colorPalette, content = content)
}

private val LocalJetsnackColors = staticCompositionLocalOf<AppColors> {
    error("No AppColorPalette provided")
}

/**
 * A Material [Colors] implementation which sets all colors to [debugColor] to discourage usage of
 * [MaterialTheme.colors] in preference to [AppTheme.colors].
 */
fun debugColors(
    isDarkTheme: Boolean,
    debugColor: Color = Color.Magenta
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !isDarkTheme
)
