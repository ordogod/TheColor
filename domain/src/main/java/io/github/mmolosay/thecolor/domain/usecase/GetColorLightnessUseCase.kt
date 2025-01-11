package io.github.mmolosay.thecolor.domain.usecase

import io.github.mmolosay.thecolor.domain.model.Color

/**
 * It is an interface, because the actual implementation is powered by an external library,
 * thus is implemented in Data architectural layer.
 */
interface GetColorLightnessUseCase {

    @Deprecated(
        message = "Use LAB's lightness over HSL's one because LAB is designed in alignment with human perception.",
        replaceWith = ReplaceWith(expression = "labLightness()"),
        level = DeprecationLevel.WARNING,
    )
    fun Color.hslLightness(): Float

    fun Color.labLightness(): Float
}