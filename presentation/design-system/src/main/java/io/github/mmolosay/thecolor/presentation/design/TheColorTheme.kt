package io.github.mmolosay.thecolor.presentation.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.valentinilk.shimmer.LocalShimmerTheme
import androidx.compose.material3.ColorScheme as MaterialColorScheme

@Composable
fun TheColorTheme(
    colorScheme: ColorScheme = DayNightColorSchemeResolver.resolve(systemBrightness()),
    content: @Composable () -> Unit,
) {
    TheColorTheme(
        materialColorScheme = colorScheme.toMaterialColorScheme(),
        content = content,
    )
}

@Composable
fun TheColorTheme(
    materialColorScheme: MaterialColorScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalShimmerTheme provides TheColorDefaultShimmerTheme,
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = typography(),
            content = content,
        )
    }
}