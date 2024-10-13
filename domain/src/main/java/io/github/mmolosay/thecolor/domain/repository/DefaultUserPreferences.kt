package io.github.mmolosay.thecolor.domain.repository

import io.github.mmolosay.thecolor.domain.model.UserPreferences

/**
 * Stores default values of user preferences.
 * They are used when there's no user-overridden value defined.
 */
object DefaultUserPreferences {

    val ColorInputType: UserPreferences.ColorInputType =
        UserPreferences.ColorInputType.Hex

    val AppUiTheme: UserPreferences.UiTheme =
        UserPreferences.UiTheme.FollowsSystem
}