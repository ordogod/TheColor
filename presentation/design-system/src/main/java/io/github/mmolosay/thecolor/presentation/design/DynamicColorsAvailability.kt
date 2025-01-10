package io.github.mmolosay.thecolor.presentation.design

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object DynamicColorsAvailability {

    @ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
    fun areDynamicColorsAvailable(): Boolean =
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}