package io.github.mmolosay.thecolor.domain.repository

import io.github.mmolosay.thecolor.domain.model.ColorInputType
import io.github.mmolosay.thecolor.domain.model.UserPreferences

/**
 * Stores default values of user preferences.
 * They are used when there's no user-overridden value defined.
 */
object DefaultUserPreferences {

    val PreferredColorInputType: ColorInputType =
        ColorInputType.Hex

    val AppUiColorSchemeSet: UserPreferences.UiColorSchemeSet =
        UserPreferences.UiColorSchemeSet.DayNight

    val DynamicUiColors: UserPreferences.DynamicUiColors =
        UserPreferences.DynamicUiColors(enabled = true)

    val ResumeFromLastSearchedColorOnStartup =
        UserPreferences.ResumeFromLastSearchedColorOnStartup(enabled = true)

    val SmartBackspace =
        UserPreferences.SmartBackspace(enabled = true)

    val SelectAllTextOnTextFieldFocus =
        UserPreferences.SelectAllTextOnTextFieldFocus(enabled = false)

    val AutoProceedWithRandomizedColors =
        UserPreferences.AutoProceedWithRandomizedColors(enabled = true)
}