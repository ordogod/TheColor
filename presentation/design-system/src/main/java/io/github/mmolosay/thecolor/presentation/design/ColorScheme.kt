package io.github.mmolosay.thecolor.presentation.design

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.mmolosay.thecolor.presentation.design.Material3DynamicColorsAvailability.areDynamicColorsAvailable
import androidx.compose.material3.ColorScheme as MaterialColorScheme

/**
 * A color scheme of UI.
 * Entries of this enumeration are final, computed values that do not require any more data
 * (e.g. unlike 'DayNight' theme, see [DayNightColorSchemeResolver]).
 */
enum class ColorScheme {
    Light,
    Dark,
    DynamicLight,
    DynamicDark,
    Jungle,
    Midnight,
    ;
}

fun ColorScheme.toMaterialColorScheme(context: Context): MaterialColorScheme {
    return when (this) {
        ColorScheme.Light -> lightColorScheme
        ColorScheme.Dark -> darkColorScheme
        ColorScheme.DynamicLight -> {
            if (!areDynamicColorsAvailable()) {
                error("$this cannot be used: Dynamic color schemes are available only on Android 12+")
            }
            dynamicLightColorScheme(context)
        }
        ColorScheme.DynamicDark -> {
            if (!areDynamicColorsAvailable()) {
                error("$this cannot be used: Dynamic color schemes are available only on Android 12+")
            }
            dynamicDarkColorScheme(context)
        }
        ColorScheme.Jungle -> jungleColorScheme
        ColorScheme.Midnight -> midnightColorScheme
    }
}

private val lightColorScheme: MaterialColorScheme by lazy {
    lightColorScheme()
}

private val darkColorScheme: MaterialColorScheme by lazy {
    darkColorScheme()
}

private val jungleColorScheme: MaterialColorScheme by lazy {
    MaterialColorScheme(
        primary = Color(0xFF3C6838),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFBDF0B3),
        onPrimaryContainer = Color(0xFF002203),
        inversePrimary = Color(0xFFA2D399),
        secondary = Color(0xFF53634E),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD6E8CE),
        onSecondaryContainer = Color(0xFF111F0F),
        tertiary = Color(0xFF38656A),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFBCEBF0),
        onTertiaryContainer = Color(0xFF002022),
        background = Color(0xFFF7FBF1),
        onBackground = Color(0xFF191D17),
        surface = Color(0xFFF7FBF1),
        onSurface = Color(0xFF191D17),
        surfaceVariant = Color(0xFFDEE5D8),
        onSurfaceVariant = Color(0xFF424940),
        surfaceTint = Color.Unspecified,
        inverseSurface = Color(0xFF2D322B),
        inverseOnSurface = Color(0xFFEFF2E9),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        outline = Color(0xFF73796F),
        outlineVariant = Color(0xFFC2C8BD),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFFF7FBF1),
        surfaceDim = Color(0xFFD8DBD2),
        surfaceContainer = Color(0xFFECEFE6),
        surfaceContainerHigh = Color(0xFFE6E9E0),
        surfaceContainerHighest = Color(0xFFE0E4DA),
        surfaceContainerLow = Color(0xFFF2F5EB),
        surfaceContainerLowest = Color(0xFFFFFFFF),
    )
}

private val midnightColorScheme: MaterialColorScheme by lazy {
    MaterialColorScheme(
        primary = Color(0xFF9CCBFB),
        onPrimary = Color(0xFF003354),
        primaryContainer = Color(0xFF114A73),
        onPrimaryContainer = Color(0xFFCFE5FF),
        inversePrimary = Color(0xFF31628D),
        secondary = Color(0xFFB9C8DA),
        onSecondary = Color(0xFF243240),
        secondaryContainer = Color(0xFF3A4857),
        onSecondaryContainer = Color(0xFFD5E4F7),
        tertiary = Color(0xFFD4BEE6),
        onTertiary = Color(0xFF392A49),
        tertiaryContainer = Color(0xFF504061),
        onTertiaryContainer = Color(0xFFEFDBFF),
        background = Color(0xFF101418),
        onBackground = Color(0xFFE0E2E8),
        surface = Color(0xFF101418),
        onSurface = Color(0xFFE0E2E8),
        surfaceVariant = Color(0xFF42474E),
        onSurfaceVariant = Color(0xFFC2C7CF),
        surfaceTint = Color.Unspecified,
        inverseSurface = Color(0xFFE0E2E8),
        inverseOnSurface = Color(0xFF2D3135),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = Color(0xFF8C9199),
        outlineVariant = Color(0xFF42474E),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF36393E),
        surfaceDim = Color(0xFF101418),
        surfaceContainer = Color(0xFF1C2024),
        surfaceContainerHigh = Color(0xFF272A2F),
        surfaceContainerHighest = Color(0xFF32353A),
        surfaceContainerLow = Color(0xFF181C20),
        surfaceContainerLowest = Color(0xFF0B0E12),
    )
}

/*
 * Animating each color of MaterialColorScheme using 'animateAsState()' is expensive.
 * There's a room for improvement TODO: improve performance
 */
@Composable
fun MaterialColorScheme.animateColors(): MaterialColorScheme {
    val animationSpec = tween<Color>(
        durationMillis = 400,
        easing = LinearOutSlowInEasing,
    )

    @Suppress("AnimateAsStateLabel")
    @Composable
    fun Color.animateAsState() =
        animateColorAsState(
            targetValue = this,
            animationSpec = animationSpec,
        )

    return this.copy(
        primary = this.primary.animateAsState().value,
        onPrimary = this.onPrimary.animateAsState().value,
        primaryContainer = this.primaryContainer.animateAsState().value,
        onPrimaryContainer = this.onPrimaryContainer.animateAsState().value,
        inversePrimary = this.inversePrimary.animateAsState().value,
        secondary = this.secondary.animateAsState().value,
        onSecondary = this.onSecondary.animateAsState().value,
        secondaryContainer = this.secondaryContainer.animateAsState().value,
        onSecondaryContainer = this.onSecondaryContainer.animateAsState().value,
        tertiary = this.tertiary.animateAsState().value,
        onTertiary = this.onTertiary.animateAsState().value,
        tertiaryContainer = this.tertiaryContainer.animateAsState().value,
        onTertiaryContainer = this.onTertiaryContainer.animateAsState().value,
        background = this.background.animateAsState().value,
        onBackground = this.onBackground.animateAsState().value,
        surface = this.surface.animateAsState().value,
        onSurface = this.onSurface.animateAsState().value,
        surfaceVariant = this.surfaceVariant.animateAsState().value,
        onSurfaceVariant = this.onSurfaceVariant.animateAsState().value,
        surfaceTint = this.surfaceTint.animateAsState().value,
        inverseSurface = this.inverseSurface.animateAsState().value,
        inverseOnSurface = this.inverseOnSurface.animateAsState().value,
        error = this.error.animateAsState().value,
        onError = this.onError.animateAsState().value,
        errorContainer = this.errorContainer.animateAsState().value,
        onErrorContainer = this.onErrorContainer.animateAsState().value,
        outline = this.outline.animateAsState().value,
        outlineVariant = this.outlineVariant.animateAsState().value,
        scrim = this.scrim.animateAsState().value,
        surfaceBright = this.surfaceBright.animateAsState().value,
        surfaceDim = this.surfaceDim.animateAsState().value,
        surfaceContainer = this.surfaceContainer.animateAsState().value,
        surfaceContainerHigh = this.surfaceContainerHigh.animateAsState().value,
        surfaceContainerHighest = this.surfaceContainerHighest.animateAsState().value,
        surfaceContainerLow = this.surfaceContainerLow.animateAsState().value,
        surfaceContainerLowest = this.surfaceContainerLowest.animateAsState().value,
    )
}