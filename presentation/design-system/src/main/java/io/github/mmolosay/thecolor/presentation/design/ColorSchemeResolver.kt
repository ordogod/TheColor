package io.github.mmolosay.thecolor.presentation.design

/**
 * Defines a strategy of obtaining a [ColorScheme] according to provided parameters.
 *
 * Brightness is usually inferred from system UI mode (see [systemBrightness]).
 */
fun interface ColorSchemeResolver {
    fun resolve(
        brightness: Brightness,
        useDynamicColorSchemes: Boolean,
    ): ColorScheme
}

/**
 * A [ColorSchemeResolver] that chooses between [ColorScheme.Light] and [ColorScheme.Dark].
 */
object DayNightColorSchemeResolver : ColorSchemeResolver {

    override fun resolve(
        brightness: Brightness,
        useDynamicColorSchemes: Boolean,
    ): ColorScheme =
        when (brightness) {
            Brightness.Light -> when (useDynamicColorSchemes) {
                true -> ColorScheme.LightDynamic
                false -> ColorScheme.Light
            }
            Brightness.Dark -> when (useDynamicColorSchemes) {
                true -> ColorScheme.DarkDynamic
                false -> ColorScheme.Dark
            }
        }
}