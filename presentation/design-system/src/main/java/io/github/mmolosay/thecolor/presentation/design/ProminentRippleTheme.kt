package io.github.mmolosay.thecolor.presentation.design

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Replicates default [MaterialRippleTheme][androidx.compose.material3.MaterialRippleTheme] (which is private in file),
 * but uses more prominent alpha values.
 * Usually used with [ColorsOnTintedSurface].
 *
 * Compare values of [StateTokens][androidx.compose.material3.tokens.StateTokens] used by `MaterialRippleTheme`
 * with values returned from [RippleTheme.defaultRippleAlpha].
 */
@Immutable
object ProminentRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color =
        LocalContentColor.current

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleAlpha

    /**
     * Based off of [LightThemeHighContrastRippleAlpha][androidx.compose.material.ripple.LightThemeHighContrastRippleAlpha],
     * which is designed for colored surfaces.
     */
    private val RippleAlpha = RippleAlpha(
        pressedAlpha = 0.24f,
        focusedAlpha = 0.24f,
        draggedAlpha = 0.16f,
        hoveredAlpha = 0.08f
    )
}