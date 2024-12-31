package io.github.mmolosay.thecolor.presentation.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import com.valentinilk.shimmer.defaultShimmerTheme

val TheColorDefaultShimmerTheme = defaultShimmerTheme.copy(
    animationSpec = infiniteRepeatable(
        animation = tween(
            durationMillis = 800,
            easing = LinearEasing,
            delayMillis = 200,
        ),
        repeatMode = RepeatMode.Restart,
    ),
)