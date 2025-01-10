package io.github.mmolosay.thecolor.presentation.design

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * Checks whether dynamic colors from `Material 3` library are available to be used
 * on the current device.
 */
object Material3DynamicColorsAvailability {

    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
    fun areDynamicColorsAvailable(): Boolean =
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}